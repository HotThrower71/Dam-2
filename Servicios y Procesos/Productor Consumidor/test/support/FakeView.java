package test.support;

import presenter.IPresenter;
import view.IView;

// Vista falsa: apunta si le han dado el presenter y si la han arrancado
public class FakeView implements IView {
    public IPresenter presenter;
    public boolean started;

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void start() {
        started = true;
    }
}
