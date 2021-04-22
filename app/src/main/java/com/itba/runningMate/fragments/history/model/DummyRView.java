package com.itba.runningMate.fragments.history.model;

public class DummyRView {
    private String title; // FIXME: Dejamos un titulo? quizas con la fecha
    private String content; // FIXME: Esto va a ser el objeto de mapa
    //TODO: Agregar detalles, como fecha, tiempo, estadisticas etc.


    public DummyRView(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
