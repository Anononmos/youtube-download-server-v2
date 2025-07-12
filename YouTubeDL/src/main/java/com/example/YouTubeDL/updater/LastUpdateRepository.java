package com.example.YouTubeDL.updater;

public interface LastUpdateRepository {
    
    public int updateDate();

    public boolean canUpdate();
}
