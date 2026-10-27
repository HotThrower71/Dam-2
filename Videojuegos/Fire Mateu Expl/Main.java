import javax.swing.SwingUtilities;

import src.model.IModel;
import src.view.IView;
import src.view.View;
import src.model.Model;
import src.presenter.IPresenter;
import src.presenter.Presenter;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IModel model = new Model();
            IView view = new View();
            IPresenter presenter = new Presenter(model, view);

            // Ejecucion total
            presenter.start();
        });
    }
}
