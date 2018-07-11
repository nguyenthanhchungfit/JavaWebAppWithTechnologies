/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift_services;

import server_data.DBSongModel;
import elastic_search_engine.ESESong;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kafka.ProducerKafka;
import models.Song;
import models.SongResult;
import org.apache.thrift.TException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cpu11165-local
 */
public class SongServicesImpl implements SongServices.Iface{

    private ESESong eseSong = new ESESong();

    @Override
    public SongResult getSongById(String id) throws TException {
        return DBSongModel.getSongById(id);
    }

    @Override
    public List<Song> getSongsSearchAPIByName(String name) throws TException {
        List<Song> listSong = DBSongModel.getSongsSearchAPIByName(name);
        if(listSong.isEmpty()){
            ProducerKafka.send("song_lookup", ProducerKafka.count + "", name);
            try {
                Thread.sleep(2000);
                listSong = DBSongModel.getSongsSearchAPIByName(name);  
            } catch (InterruptedException ex) {
                Logger.getLogger(SongServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                return listSong;
            }
        }else{
            return listSong;
        }
    }

    @Override
    public List<Song> getSongsSearchESEByName(String name) throws TException {
        try {
            return eseSong.getSongsSearchByName(name);
        } catch (IOException ex) {
            Logger.getLogger(SongServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SongServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
}
