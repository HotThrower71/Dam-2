import model.IModel;
import model.Model;
import presenter.IPresenter;
import presenter.Presenter;
import view.IView;
import view.View;

// Lo que se ejecuta en el hilo grafico de Swing: crea las tres piezas y arranca
public class Launcher implements Runnable {
    @Override
    public void run() {
        IModel model = new Model();
        IView view = new View();
        IPresenter presenter = new Presenter(model, view);

        presenter.start();
    }
}
