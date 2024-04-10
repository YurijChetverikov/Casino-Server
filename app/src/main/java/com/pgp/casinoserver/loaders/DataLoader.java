package com.pgp.casinoserver.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pgp.casinoserver.core.Game;
import com.pgp.casinoserver.core.GameDataBlock;
import com.pgp.casinoserver.core.GameSession;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.core.Transaction;
import com.pgp.casinoserver.core.TransactionType;
import com.pgp.casinoserver.ui.MainActivity;
import com.pgp.casinoserver.utils.BinaryUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataLoader {
    private static DataLoader Singleton;


    public ArrayList<Player> Players = new ArrayList<Player>();
    public ArrayList<Game> Games = new ArrayList<Game>();
    public Map<Integer,Transaction> Transactions = new HashMap<Integer,Transaction>(0);
    public ArrayList<Player> PlayersInGameRoom = new ArrayList<Player>();
    private Bitmap casinoBitmap = null;
    public byte[] CompressedBitmap = new byte[0];
    public Game CurrentGame = null;

    public byte TransactionComission = 0; // В процентах




    public int CasinoPlayerID = -1;


    final String PLAYERS_TABLE_FILENAME = "pl.bin";
    final String BIG_DATA_FILENAME = "big_data.dat";
    final String CASINO_IMAGE_FILENAME = "pic.dat";


//    public CacheProcessor(){
//        Singleton = this;
//
//        Players.put(0, new Player(0,"Ваня", 100));
//        Players.put(1, new Player(1,"Петя", 300));
//        CasinoPlayerID = 0;
//    }


    public static DataLoader Singleton(){
        if (Singleton == null){
            Singleton = new DataLoader();
        }

        return Singleton;
    }

    // Пишет и файл и игроками, и файл с их данными
    public void WriteAllCahce(@NonNull Context context) throws IOException {
        setSavingTitle(true);
        WriteTableCache(context);
        setSavingTitle(true);
        for (Player pl: Players){
            WriteDataCahce(context, pl);
            setSavingTitle(true);
        }
        setSavingTitle(false);
    }

    public void WriteTableCache(@NonNull Context context) throws IOException{
        setSavingTitle(true);
        FileOutputStream stream = context.openFileOutput(PLAYERS_TABLE_FILENAME, Context.MODE_PRIVATE);
        stream.write(getPlayersTable());
        stream.close();
        setSavingTitle(false);
    }


    // Пишет и файл с данными игроков
    public void WriteDataCahce(@NonNull Context context, @NonNull Player player) throws IOException {
        setSavingTitle(true);
        FileOutputStream stream = context.openFileOutput(String.format("pl%d.dat", player.ID), Context.MODE_PRIVATE);
        stream.write(getPlayerData(player));
        stream.close();
        setSavingTitle(false);
    }

    public void WriteCasinoImageCache(@NonNull Context context) throws IOException{
        FileOutputStream stream = context.openFileOutput(CASINO_IMAGE_FILENAME, Context.MODE_PRIVATE);
        if (CompressedBitmap != null){
            stream.write(CompressedBitmap);
        }else{
            stream.write(0);
        }
        stream.close();
    }

    public void WriteBigDataCache(@NonNull Context context) throws IOException{
        setSavingTitle(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(BinaryUtils.Int2Bytes(Transactions.size()));
        for (Map.Entry<Integer, Transaction> t: Transactions.entrySet()){
            outputStream.write(BinaryUtils.Int2Bytes(t.getKey()));
            outputStream.write(BinaryUtils.Int2Bytes(t.getValue().Sender.ID));
            outputStream.write(BinaryUtils.Int2Bytes(t.getValue().Receiver.ID));
            outputStream.write(BinaryUtils.Int2Bytes(t.getValue().Amount));
            outputStream.write(BinaryUtils.Int2Bytes(t.getValue().SenderPaid));
            outputStream.write((byte)t.getValue().Type.ordinal());
            outputStream.write(BinaryUtils.WriteString(t.getValue().Description));
        }

        FileOutputStream stream = context.openFileOutput(BIG_DATA_FILENAME, Context.MODE_PRIVATE);
        stream.write(outputStream.toByteArray());
        stream.close();

        setSavingTitle(false);
    }


    public CacheReadingResult ReadCache(@NonNull Context context) throws IOException {
        // Алгоритм такой:
        // Считываем игроков
        // Считываем big data (то есть все транзакции)
        // Считываем данные игроком (то есть id транзакций, принадлежащих им, игровые сессии)

        FileInputStream fin = null;
        byte[] bytes = null;
        try{
            fin = context.openFileInput(PLAYERS_TABLE_FILENAME);
        }catch(Exception ex){
            return CacheReadingResult.NOT_READED;
        }
        bytes = new byte[fin.available()];
        fin.read(bytes);
        fin.close();
        ByteBuffer b = ByteBuffer.wrap(bytes);
        b.position(16);
        Player pl = parsePlayer(b);
        if (pl != null){
            pl = parsePlayerData(pl, context);
            CasinoPlayerID = pl.ID;
            pl.IsCasino = true;
            Players.add(pl);

            byte gamesCount = b.get();
            for(byte i = 0; i < gamesCount; i++){
                Game g = new Game();
                g.ID = b.get();
                g.Name = BinaryUtils.ReadString(b);
                Games.add(g);
            }

            TransactionComission = b.get();

            int playersCount = b.getInt();
            for(int i = 0; i < playersCount; i++){
                pl = parsePlayer(b);
                pl = parsePlayerData(pl, context);
                Players.add(pl);
            }

            // Теперь считываем big data

            parseBigData(context);


            // Считываем картинку казина

            fin = context.openFileInput(CASINO_IMAGE_FILENAME);
            if (fin.available() > 4){
                bytes = new byte[fin.available()];
                fin.read(bytes);
                fin.close();
                b = ByteBuffer.wrap(bytes);
                CompressedBitmap = b.array();
                casinoBitmap = BitmapFactory.decodeByteArray(CompressedBitmap, 0, CompressedBitmap.length);
            }






            return CacheReadingResult.READED_SUCCESSFULLY;
        }
        return CacheReadingResult.READED_WITH_ERRORS;
    }


    @Nullable
    private Player parsePlayer(@NonNull ByteBuffer b) {
        try{
            Player pl = new Player(b.getInt(), b.getInt(), BinaryUtils.ReadString(b), b.getInt());
            byte flags = b.get();
            pl.TransactionsLeft = b.get();
            pl.RegistrationDate = new Date(b.getLong());
            return pl;
        }catch(IOException ex){
            Log.e("package:mine", ex.toString());
        }
        return null;
    }

    private Player parsePlayerData(Player pl, Context context){
        try{
            FileInputStream fin = null;
            byte[] bytes = null;
            fin = context.openFileInput(String.format("pl%d.dat", pl.ID));

            if (fin.available() >= 8){
                bytes = new byte[fin.available()];
                fin.read(bytes);
                ByteBuffer b = ByteBuffer.wrap(bytes);
                int gameSessions = b.getInt();
                for (int i = 0; i < gameSessions; i++){
                    GameSession gs = new GameSession(b.getLong());
                    gs.EndDate = b.getLong();
                    gs.BalanceChange = b.getInt();
                    gs.WinsCount = b.getInt();
                    gs.LoseCount = b.getInt();
                    int dataBlockCount = b.getInt();
                    for (int j = 0; j < dataBlockCount; j++){
                        GameDataBlock g = new GameDataBlock(GetGameByID(b.get()), b.getLong());
                        g.EndDate = b.getLong();
                        g.WinsCount = b.getInt();
                        g.LoseCount = b.getInt();
                        gs.GameBlocks.add(g);
                    }
                    pl.GameSessions.add(gs);
                }
            }
        }catch (Exception ex){
            Log.e("package:mine", ex.toString());
        }

        return pl;
    }

    @NonNull
    private byte[] getPlayerData(@NonNull Player pl) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(BinaryUtils.Int2Bytes(pl.GameSessions.size()));
        for (GameSession gs : pl.GameSessions){
            outputStream.write(BinaryUtils.Long2Bytes(gs.StartDate));
            outputStream.write(BinaryUtils.Long2Bytes(gs.EndDate));
            outputStream.write(BinaryUtils.Int2Bytes(gs.BalanceChange));
            outputStream.write(BinaryUtils.Int2Bytes(gs.WinsCount));
            outputStream.write(BinaryUtils.Int2Bytes(gs.LoseCount));
            outputStream.write(BinaryUtils.Int2Bytes(gs.GameBlocks.size()));
            for (GameDataBlock g : gs.GameBlocks){
                outputStream.write(g.Game.ID);
                outputStream.write(BinaryUtils.Long2Bytes(g.StartDate));
                outputStream.write(BinaryUtils.Long2Bytes(g.EndDate));
                outputStream.write(BinaryUtils.Int2Bytes(g.WinsCount));
                outputStream.write(BinaryUtils.Int2Bytes(g.LoseCount));
            }
        }

        return outputStream.toByteArray();
    }


    @NonNull
    private byte[] getPlayersTable() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[16]); // резерв
        if (GetCasinoPlayer() != null){
            outputStream = writePlayer(GetCasinoPlayer(), outputStream);
            outputStream.write(Games.size());

            for (Game g: Games){
                outputStream.write(g.ID);
                outputStream.write(BinaryUtils.WriteString(g.Name));
            }

            outputStream.write(TransactionComission);
            outputStream.write(BinaryUtils.Int2Bytes(Players.size() - 1));

            for (Player pl: Players) {
                if (pl.ID != CasinoPlayerID){
                    outputStream = writePlayer(pl, outputStream);
                }
            }
        }

        byte[] res = outputStream.toByteArray();

        outputStream.close();

        return res;
    }


    private void parseBigData(Context context){
        FileInputStream fin = null;
        byte[] bytes = null;
        try{
            fin = context.openFileInput(BIG_DATA_FILENAME);
            bytes = new byte[fin.available()];
            fin.read(bytes);
            ByteBuffer b = ByteBuffer.wrap(bytes);

            int transCount = b.getInt();

            for(int i = 0; i < transCount; i++){
                Transaction t = new Transaction();
                int transID = b.getInt();
                int senderId = b.getInt();
                int receiverId = b.getInt();
                t.Sender = GetPlayerById(senderId);
                t.Receiver = GetPlayerById(receiverId);
                t.Amount = b.getInt();
                t.SenderPaid = b.getInt();
                t.Type = TransactionType.Get((int)b.get());
                t.Description = BinaryUtils.ReadString(b);
                Transactions.put(transID, t);
                GetPlayerById(senderId).Transactions.add(Transactions.get(transID));
                GetPlayerById(receiverId).Transactions.add(Transactions.get(transID));
            }


        }catch(Exception ex){
            Log.e("package:mine", ex.toString());
        }
    }

    private ByteArrayOutputStream writePlayer(@NonNull Player pl, @NonNull ByteArrayOutputStream outputStream) throws IOException {
        outputStream.write(BinaryUtils.Int2Bytes(pl.ID));
        outputStream.write(BinaryUtils.Int2Bytes(pl.Password));
        outputStream.write(BinaryUtils.WriteString(pl.Name));
        outputStream.write(BinaryUtils.Int2Bytes(pl.Balance));
        outputStream.write(0); // Флаги
        outputStream.write(pl.TransactionsLeft);
        outputStream.write(BinaryUtils.Long2Bytes(pl.RegistrationDate.getTime()));

        return outputStream;
    }


    @Nullable
    public Player GetPlayerById(int id){
        for(Player pl: Players){
            if (pl.ID == id){
                return pl;
            }
        }

        return null;
    }

    public int GetNextFreeID(){
        return Players.size();
    }

    // Если возвратит 255 => все id заняты
    public byte GetNextFreeGameID(){
        boolean correct = true;
        for (byte i = 0; i < 255; i++){
            for (Game g: Games) {
                if (g.ID == i){
                    correct = false;
                    break;
                }
            }
            if (correct){
                return i;
            }
            correct = true;
        }

        return (byte)255;
    }

    public int getNextTransactionID(){
        if (Transactions.keySet().size() == 0){
            return 0;
        }
        return (int)Transactions.keySet().toArray()[Transactions.keySet().size() - 1];
    }


    public Game GetGameByID(byte id){
        for(Game g: Games){
            if (g.ID == id){
                return g;
            }
        }

        return null;
    }

    public Player GetCasinoPlayer(){
        return GetPlayerById(CasinoPlayerID);
    }

    public Bitmap getCasinoBitmap(){
        return casinoBitmap;
    }

    public void setCasinoBitmap(@NonNull Bitmap newImage){
        casinoBitmap = newImage.copy(newImage.getConfig(), true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newImage.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        CompressedBitmap = stream.toByteArray();
        newImage.recycle();


    }


    private boolean isSaving = false;
    private String oldTitle;
    private void setSavingTitle(boolean saving){
        if (MainActivity.Singleton() != null){
            if (isSaving != saving){
                if (isSaving == true){
                    //MainActivity.Singleton().setToolbarTitle(oldTitle);
                }else{
                    //oldTitle = MainActivity.Singleton().getToolbarTitle();
                    //MainActivity.Singleton().setToolbarTitle( oldTitle + " (Сохр.)");
                }
            }
        }
        isSaving = saving;
    }
}

