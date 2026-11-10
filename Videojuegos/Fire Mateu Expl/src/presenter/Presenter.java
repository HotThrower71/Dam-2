package src.presenter;

import java.awt.image.BufferedImage;

import src.model.IModel;
import src.view.IView;

// Intermediario: modelo y vista no se conocen entre si, los dos hablan con el Presenter
public class Presenter implements IPresenter {
    private final IModel model;

    private final IView view;


    public Presenter (IModel model,IView view){
        this.model = model;
        this.view = view;
    }

    @Override
    public void start(){
        view.setPresenter(this);
        model.start();
        view.start();
    }

    @Override
    public BufferedImage getImage(){ return model.getImage();}
}
