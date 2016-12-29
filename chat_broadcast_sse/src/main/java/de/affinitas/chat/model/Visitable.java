package de.affinitas.chat.model;

public interface Visitable<T> {
    void accept(Visitor<T> visitor);
}
