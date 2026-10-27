package src.view;

import src.presenter.IPresenter;

public interface IView {
    void setPresenter(IPresenter presenter);
    void start();
}
