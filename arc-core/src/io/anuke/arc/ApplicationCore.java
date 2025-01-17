package io.anuke.arc;

import io.anuke.arc.files.FileHandle;

public abstract class ApplicationCore implements ApplicationListener{
    protected ApplicationListener[] modules = {};

    public void add(ApplicationListener module){
        ApplicationListener[] news = new ApplicationListener[modules.length + 1];
        news[news.length - 1] = module;
        System.arraycopy(modules, 0, news, 0, modules.length);
        modules = news;
    }

    public abstract void setup();

    @Override
    public void init(){
        setup();

        for(ApplicationListener listener : modules){
            listener.init();
        }
    }

    @Override
    public void resize(int width, int height){
        for(ApplicationListener listener : modules){
            listener.resize(width, height);
        }
    }

    @Override
    public void update(){
        for(ApplicationListener listener : modules){
            listener.update();
        }
    }

    @Override
    public void pause(){
        for(ApplicationListener listener : modules){
            listener.pause();
        }
    }

    @Override
    public void resume(){
        for(ApplicationListener listener : modules){
            listener.resume();
        }
    }

    @Override
    public void dispose(){
        for(ApplicationListener listener : modules){
            listener.dispose();
        }
    }

    @Override
    public void fileDropped(FileHandle file){
        for(ApplicationListener listener : modules){
            listener.fileDropped(file);
        }
    }
}
