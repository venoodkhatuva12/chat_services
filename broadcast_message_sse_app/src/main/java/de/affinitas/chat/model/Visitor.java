package de.affinitas.chat.model;

public interface Visitor<T> {

    void visit(T t);

}