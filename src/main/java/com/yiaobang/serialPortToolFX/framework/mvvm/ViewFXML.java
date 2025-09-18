package com.yiaobang.serialporttoolfx.framework.mvvm;

import com.yiaobang.serialporttoolfx.utils.I18nUtils;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML 视图基类
 *
 * @author Y
 * @date 2024/05/14
 */
public abstract class ViewFXML<ViewModelType extends ViewModel> implements Initializable {
    protected ViewModelType viewModel;
    protected abstract ViewModelType createViewModel();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = this.createViewModel();
        // Initialize i18n if resources are not provided
        if (resources == null) {
            // Load appropriate resource bundle based on current locale
            try {
                String bundleName = "com.yiaobang.serialportfx.i18n.messages";
                String suffix = switch (I18nUtils.getCurrentLocale().getLanguage()) {
                    case "zh" -> "_ZH";
                    case "ja" -> "_JA";
                    default -> "_EN";
                };
                resources = ResourceBundle.getBundle(bundleName + suffix);
            } catch (Exception e) {
                // Fallback to English if loading fails
                resources = ResourceBundle.getBundle("com.yiaobang.serialportfx.i18n.messages_EN");
            }
        }
    }
}