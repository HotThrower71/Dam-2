package View;

import java.awt.image.BufferedImage;

import Presenter.IPresenter;

/** Lo que el presentador puede pedirle a la vista. */
public interface IView {
    void updateImage(BufferedImage image);
    void setPresenter(IPresenter presenter);
}
