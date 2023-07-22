package databases;

public class WrongTypeException extends Exception {
	WrongTypeException() {
		super(Translations.WRONG_TYPE.getText());
	}
}
