package io.anuke.arc.backends.gwt.preloader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import io.anuke.arc.Files.FileType;
import io.anuke.arc.backends.gwt.GwtFileHandle;
import io.anuke.arc.backends.gwt.preloader.AssetDownloader.AssetLoaderListener;
import io.anuke.arc.backends.gwt.preloader.AssetFilter.AssetType;
import io.anuke.arc.collection.Array;
import io.anuke.arc.collection.ObjectMap;
import io.anuke.arc.files.FileHandle;
import io.anuke.arc.util.ArcRuntimeException;

import java.io.*;

public class Preloader{

    public final String baseUrl;
    public ObjectMap<String, Void> directories = new ObjectMap<String, Void>();
    public ObjectMap<String, ImageElement> images = new ObjectMap<String, ImageElement>();
    public ObjectMap<String, Void> audio = new ObjectMap<String, Void>();
    public ObjectMap<String, String> texts = new ObjectMap<String, String>();
    public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();

    public Preloader(String newBaseURL){

        baseUrl = newBaseURL;

        // trigger copying of assets and creation of assets.txt
        GWT.create(PreloaderBundle.class);
    }

    public void preload(final String assetFileUrl, final PreloaderCallback callback){
        final AssetDownloader loader = new AssetDownloader();

        loader.loadText(baseUrl + assetFileUrl, new AssetLoaderListener<String>(){
            @Override
            public void onProgress(double amount){
            }

            @Override
            public void onFailure(){
                callback.error(assetFileUrl);
            }

            @Override
            public void onSuccess(String result){
                String[] lines = result.split("\n");
                Array<Asset> assets = new Array<Asset>(lines.length);
                for(String line : lines){
                    String[] tokens = line.split(":");
                    if(tokens.length != 4){
                        throw new ArcRuntimeException("Invalid assets description file.");
                    }
                    AssetType type = AssetType.Text;
                    if(tokens[0].equals("i")) type = AssetType.Image;
                    if(tokens[0].equals("b")) type = AssetType.Binary;
                    if(tokens[0].equals("a")) type = AssetType.Audio;
                    if(tokens[0].equals("d")) type = AssetType.Directory;
                    long size = Long.parseLong(tokens[2]);
                    if(type == AssetType.Audio && !loader.isUseBrowserCache()){
                        size = 0;
                    }
                    assets.add(new Asset(tokens[1].trim(), type, size, tokens[3]));
                }
                final PreloaderState state = new PreloaderState(assets);
                for(int i = 0; i < assets.size; i++){
                    final Asset asset = assets.get(i);

                    if(contains(asset.url)){
                        asset.loaded = asset.size;
                        asset.succeed = true;
                        continue;
                    }

                    loader.load(baseUrl + asset.url, asset.type, asset.mimeType, new AssetLoaderListener<Object>(){
                        @Override
                        public void onProgress(double amount){
                            asset.loaded = (long)amount;
                            callback.update(state);
                        }

                        @Override
                        public void onFailure(){
                            asset.failed = true;
                            callback.error(asset.url);
                            callback.update(state);
                        }

                        @Override
                        public void onSuccess(Object result){
                            switch(asset.type){
                                case Text:
                                    texts.put(asset.url, (String)result);
                                    break;
                                case Image:
                                    images.put(asset.url, (ImageElement)result);
                                    break;
                                case Binary:
                                    binaries.put(asset.url, (Blob)result);
                                    break;
                                case Audio:
                                    audio.put(asset.url, null);
                                    break;
                                case Directory:
                                    directories.put(asset.url, null);
                                    break;
                            }
                            asset.succeed = true;
                            callback.update(state);
                        }
                    });
                }
                callback.update(state);
            }
        });
    }

    public InputStream read(String url){
        if(texts.containsKey(url)){
            try{
                return new ByteArrayInputStream(texts.get(url).getBytes("UTF-8"));
            }catch(UnsupportedEncodingException e){
                return null;
            }
        }
        if(images.containsKey(url)){
            return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
        }
        if(binaries.containsKey(url)){
            return binaries.get(url).read();
        }
        if(audio.containsKey(url)){
            return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
        }
        return null;
    }

    public boolean contains(String url){
        return texts.containsKey(url) || images.containsKey(url) || binaries.containsKey(url) || audio.containsKey(url) || directories.containsKey(url);
    }

    public boolean isText(String url){
        return texts.containsKey(url);
    }

    public boolean isImage(String url){
        return images.containsKey(url);
    }

    public boolean isBinary(String url){
        return binaries.containsKey(url);
    }

    public boolean isAudio(String url){
        return audio.containsKey(url);
    }

    public boolean isDirectory(String url){
        return directories.containsKey(url);
    }

    private boolean isChild(String path, String url){
        return path.startsWith(url) && (path.indexOf('/', url.length() + 1) < 0);
    }

    public FileHandle[] list(String url){
        Array<FileHandle> files = new Array<FileHandle>();
        for(String path : texts.keys()){
            if(isChild(path, url)){
                files.add(new GwtFileHandle(this, path, FileType.Internal));
            }
        }
        FileHandle[] list = new FileHandle[files.size];
        System.arraycopy(files.items, 0, list, 0, list.length);
        return list;
    }

    public FileHandle[] list(String url, FileFilter filter){
        Array<FileHandle> files = new Array<FileHandle>();
        for(String path : texts.keys()){
            if(isChild(path, url) && filter.accept(new File(path))){
                files.add(new GwtFileHandle(this, path, FileType.Internal));
            }
        }
        FileHandle[] list = new FileHandle[files.size];
        System.arraycopy(files.items, 0, list, 0, list.length);
        return list;
    }

    public FileHandle[] list(String url, FilenameFilter filter){
        Array<FileHandle> files = new Array<FileHandle>();
        for(String path : texts.keys()){
            if(isChild(path, url) && filter.accept(new File(url), path.substring(url.length() + 1))){
                files.add(new GwtFileHandle(this, path, FileType.Internal));
            }
        }
        FileHandle[] list = new FileHandle[files.size];
        System.arraycopy(files.items, 0, list, 0, list.length);
        return list;
    }

    public FileHandle[] list(String url, String suffix){
        Array<FileHandle> files = new Array<FileHandle>();
        for(String path : texts.keys()){
            if(isChild(path, url) && path.endsWith(suffix)){
                files.add(new GwtFileHandle(this, path, FileType.Internal));
            }
        }
        FileHandle[] list = new FileHandle[files.size];
        System.arraycopy(files.items, 0, list, 0, list.length);
        return list;
    }

    public long length(String url){
        if(texts.containsKey(url)){
            try{
                return texts.get(url).getBytes("UTF-8").length;
            }catch(UnsupportedEncodingException e){
                return texts.get(url).getBytes().length;
            }
        }
        if(images.containsKey(url)){
            return 1; // FIXME, sensible?
        }
        if(binaries.containsKey(url)){
            return binaries.get(url).length();
        }
        if(audio.containsKey(url)){
            return 1; // FIXME sensible?
        }
        return 0;
    }

    public interface PreloaderCallback{

        void update(PreloaderState state);

        void error(String file);

    }

    public static class Asset{
        public final String url;
        public final AssetType type;
        public final long size;
        public final String mimeType;
        public boolean succeed;
        public boolean failed;
        public long loaded;
        public Asset(String url, AssetType type, long size, String mimeType){
            this.url = url;
            this.type = type;
            this.size = size;
            this.mimeType = mimeType;
        }
    }

    public static class PreloaderState{

        public final Array<Asset> assets;

        public PreloaderState(Array<Asset> assets){
            this.assets = assets;
        }

        public long getDownloadedSize(){
            long size = 0;
            for(int i = 0; i < assets.size; i++){
                Asset asset = assets.get(i);
                size += (asset.succeed || asset.failed) ? asset.size : Math.min(asset.size, asset.loaded);
            }
            return size;
        }

        public long getTotalSize(){
            long size = 0;
            for(int i = 0; i < assets.size; i++){
                Asset asset = assets.get(i);
                size += asset.size;
            }
            return size;
        }

        public float getProgress(){
            long total = getTotalSize();
            return total == 0 ? 1 : (getDownloadedSize() / (float)total);
        }

        public boolean hasEnded(){
            return getDownloadedSize() == getTotalSize();
        }

    }

}
