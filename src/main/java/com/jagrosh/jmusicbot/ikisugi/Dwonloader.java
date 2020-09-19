package com.jagrosh.jmusicbot.ikisugi;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Dwonloader {
    private static Dwonloader INSTANS;

    public static void init() {
        INSTANS = new Dwonloader();
        getCashFolderPath().toFile().mkdirs();
    }

    public static Dwonloader instans() {
        return INSTANS;
    }

    public static Path getCashFolderPath() {
        return Paths.get("cash");
    }

    public void dwonloadStart(QueuedTrack qtrack) {

        if (qtrack.getTrack().getDuration() == 9223372036854775807l)
            return;

        DownloadThread dt = new DownloadThread(qtrack);
        dt.start();
    }

    public static class DownloadThread extends Thread {

        private final QueuedTrack qtrack;

        public DownloadThread(QueuedTrack qtrack) {
            this.qtrack = qtrack;
        }

        @Override
        public void run() {
            if (qtrack != null && qtrack.getTrack() != null && qtrack.getTrack().getInfo() != null) {
                if (qtrack.getTrack().getInfo().uri.equals(qtrack.getTrack().getInfo().identifier))
                    Dwonloader.instans().dwonloadSoundFile(qtrack.getTrack().getInfo().uri);
                else
                    Dwonloader.instans().dwonloadYoutube(qtrack.getTrack().getInfo().identifier);
            }
        }
    }

    public boolean existsSoundFile(String urlst) {
        String cname = canFolders(urlst) + ".mp3";
        return getCashFolderPath().resolve(cname).toFile().exists();
    }

    public boolean existsYoutube(String id) {
        String cname = id + ".mp3";
        return getCashFolderPath().resolve(cname).toFile().exists();
    }

    public void dwonloadSoundFile(String urlst) {

        if (existsSoundFile(urlst))
            return;

        try {
            URL url = new URL(urlst);
            MultimediaObject mmObject = new MultimediaObject(url);
            Encoder encoder = new Encoder();
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audio);
            encoder.encode(mmObject, getCashFolderPath().resolve(canFolders(urlst) + "-tmp.mp3").toFile(), attrs);
            Mp3File mp3 = new Mp3File(getCashFolderPath().resolve(canFolders(urlst) + "-tmp.mp3"));
            if (mp3.getId3v2Tag().getTitle() == null || mp3.getId3v2Tag().getTitle().isEmpty()) {
                mp3.getId3v2Tag().setTitle(urlst);
            }
            mp3.save(getCashFolderPath().resolve(canFolders(urlst) + ".mp3").toString());
            getCashFolderPath().resolve(canFolders(urlst) + "-tmp.mp3").toFile().delete();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void dwonloadYoutube(String id) {

        if (existsYoutube(id))
            return;
        try {
            YoutubeDownloader yd = new YoutubeDownloader();
            YoutubeVideo yv = yd.getVideo(id);
            yv.download(yv.videoWithAudioFormats().get(0), getCashFolderPath().toFile(), id + "-tmp");
            MultimediaObject mmObject = new MultimediaObject(getCashFolderPath().resolve(id + "-tmp.mp4").toFile());
            Encoder encoder = new Encoder();
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audio);
            encoder.encode(mmObject, getCashFolderPath().resolve(id + "-tmp.mp3").toFile(), attrs);
            getCashFolderPath().resolve(id + "-tmp.mp4").toFile().delete();
            Mp3File mp3 = new Mp3File(getCashFolderPath().resolve(id + "-tmp.mp3"));
            mp3.getId3v2Tag().setTitle(yv.details().title());
            mp3.save(getCashFolderPath().resolve(id + ".mp3").toString());
            getCashFolderPath().resolve(id + "-tmp.mp3").toFile().delete();
        } catch (YoutubeException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
    }


    public String canFolders(String name) {
        Pattern illegalFileNamePattern = Pattern.compile("[(\\|/|:|\\*|?|\"|<|>|\\\\|)]");
        return illegalFileNamePattern.matcher(name).replaceAll("");
    }

}
