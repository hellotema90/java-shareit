package ru.practicum.shareit.exeption;

public class ConflictException extends RuntimeException{
    public ConflictException(String s){
        super(s);
    }
}
