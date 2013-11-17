import org.robotframework.javalib.library.AnnotationLibrary;

public class WebDriverLibrary extends AnnotationLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
    public static final String ROBOT_LIBRARY_VERSION = "1.0";
    
    public WebDriverLibrary() {
        super("org/robotframework/webdriverlibrary/WebDriverKeywords.class");
    }
}
