package com.zju.campustour.model.chatting.entity;


public enum FileType {

    image,
    audio,
    video,
    document,
    other;


    public static FileType getFileTypeByOrdinal(int ordinal) {

        for (FileType type : values()) {

            if (type.ordinal() == ordinal) {

                return type;
            }

        }

        return image;

    }
}
