package com.yiaobang.serialPortToolFX.javafxTool.mvvm;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class ViewFXML<ViewModelType extends ViewModel> implements Initializable {
    protected ViewModelType viewModel;
    protected abstract ViewModelType createViewModel();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = this.createViewModel();
    }
}
