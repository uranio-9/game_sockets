package co.edu.uptc.server.presenter;

import co.edu.uptc.server.interfaces.ModelInterface;
import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.server.interfaces.ViewInterface;
import co.edu.uptc.server.model.GameModel;
import co.edu.uptc.server.view.ServerFrame;

public class ServerRunner {
    private PresenterInterface presenter;
    private ModelInterface model;
    private ViewInterface view;

    public void makeMVP() {
        presenter = new ServerPresenter();
        model = new GameModel();
        view = new ServerFrame();

        presenter.setModel(model);
        presenter.setView(view);
        view.setPresenter(presenter);
    }

    public void start() {
        makeMVP();
        view.start();
    }
}
