package co.edu.uptc.client.presenter;

import co.edu.uptc.client.interfaces.ModelInterface;
import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.client.interfaces.ViewInterface;
import co.edu.uptc.client.model.ClientModel;
import co.edu.uptc.client.view.ClientFrame;

public class ClientRunner {
    private PresenterInterface presenter;
    private ModelInterface model;
    private ViewInterface view;

    public void makeMVP() {
        presenter = new ClientPresenter();
        model = new ClientModel();
        view = new ClientFrame();

        presenter.setModel(model);
        presenter.setView(view);
        view.setPresenter(presenter);
    }

    public void start() {
        makeMVP();
        view.start();
    }
}
