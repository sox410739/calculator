package calculator;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.math.BigInteger;


public class Calculator {
	boolean calculating_flag = true; // 代表現在是計算中還是計算結束
	// 獲得美台螢幕大小，根據不同螢幕調整計算機位置
	int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	// 計算機的寬高，以及寬高修飾
	int WIDTH = 350;
	int HEIGHT = 500;
	int WIDTH_MARGIN = 6;
	int WIDTH_MARGIN_END = 6;
	int HEIGHT_MARGIN = 35;
	int HEIGHT_MARGIN_END = 6;
	Frame frame; //視窗
	JLabel console; // 計算機顯示條文字
	JPanel consoleBack; // 計算機顯示條
	// 所有計算機按鍵
	JButton[] numbers = new JButton[10];
	JButton plus = new JButton("+");
	JButton minus = new JButton("-");
	JButton multiply = new JButton("x");
	JButton divide = new JButton("/");
	JButton equal = new JButton("=");
	JButton reset = new JButton("C");

	public static void main(String[] args) {
		// 程式進入點
		new Calculator();
	}
	
	public Calculator() {
		frame = new Frame("劉璟鴻");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setLayout(null);
		frame.setSize(WIDTH + WIDTH_MARGIN + WIDTH_MARGIN_END, HEIGHT + HEIGHT_MARGIN + HEIGHT_MARGIN_END);
		frame.setLocation(SCREEN_WIDTH*3/5, SCREEN_HEIGHT/8);
		consoleBack = new JPanel();
		consoleBack.setLayout(null);
		consoleBack.setLocation(WIDTH/4 + WIDTH_MARGIN, HEIGHT_MARGIN);
		consoleBack.setSize(WIDTH/4*3, HEIGHT/5);
		consoleBack.setBorder(new LineBorder(new Color(200, 200, 200), 1, false));
		consoleBack.setBackground(Color.WHITE);

		console = new JLabel("0", SwingConstants.RIGHT);
		console.setLocation(20, HEIGHT/10);
		console.setSize(WIDTH/4*3 - 30, HEIGHT/10);
		console.setFont(new Font("Dialog", Font.PLAIN, 40));
		console.setForeground(new Color(255, 160, 0));
		
		frame.add(consoleBack);
		consoleBack.add(console);
		setupButton(frame);
		frame.addKeyListener(new CalculateKeyEvent());
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	/**
	 * Handle keyEvent
	 */
	class CalculateKeyEvent extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			switch (keycode) {
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
				numberInputEvent("" + (keycode-48));
				break;
			case KeyEvent.VK_NUMPAD0:
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_NUMPAD9:
				numberInputEvent("" + (keycode-96));
				break;
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_ADD:
				operatorInputEvent("+");
				break;
			case KeyEvent.VK_MINUS:
			case 109:
				operatorInputEvent("-");
				break;
			case KeyEvent.VK_MULTIPLY:
				operatorInputEvent("x");
				break;
			case KeyEvent.VK_DIVIDE:
				operatorInputEvent("/");
				break;
			case KeyEvent.VK_ENTER:
				equalInputEvent();
				break;
			case KeyEvent.VK_ESCAPE:
				resetInputEvent();
				break;
			}
		}
	}
	
	/**
	 * 當輸入數字時觸發
	 */
	private void numberInputEvent(String numstr) {
		String formula = console.getText();
		String[] numbers = formula.split("[+-/x]");
		char lastCh = formula.charAt(formula.length()-1);
		
		if (!calculating_flag) {
			// 計算結束時
			console.setText( numstr );
			calculating_flag = true;
		} else if (new BigInteger(numbers[numbers.length-1]).equals(BigInteger.ZERO) && !isOperator(lastCh)) {
			// 避免 0 開頭
			console.setText( formula.substring(0, formula.length()-1) + numstr );
		} else {
			console.setText( formula + numstr );
		}
	}
	
	/**
	 * 輸入運算符號時觸發
	 */
	private void operatorInputEvent(String operator) {
		String formula = console.getText();
		char lastCh = formula.charAt(formula.length()-1);
		
		if (isOperator(lastCh)) {
			formula = formula.substring(0, formula.length()-1);
		}
		if (formula.equals("Divide by 0")) {
			formula = "0";
		}
		console.setText( formula + operator );
		calculating_flag = true;
	}
	
	/**
	 * 輸入等於時觸發
	 */
	private void equalInputEvent() {
		String formula = console.getText();
		char lastCh = formula.charAt(formula.length()-1);
		
		if (isOperator(lastCh)) {
			formula = formula.substring(0, formula.length()-1);
		}
		if (formula.equals("Divide by 0")) {
			formula = "0";
		}
		
		console.setText( calculate(formula) );
		calculating_flag = false;
	}
	
	/**
	 * 輸入 esc 時觸發
	 */
	private void resetInputEvent() {
		console.setText("0");
		calculating_flag = true;
	}
	
	/**
	 * 計算算式
	 */
	private String calculate(String formula) {
		Pattern level1 = Pattern.compile("[-]?[0-9]+[x/]+[0-9]+"); // 乘除
		Pattern level2 = Pattern.compile("[-]?[0-9]+[+-]+[0-9]+"); // 加減
		Matcher matcher = level1.matcher(formula);
		// 計算乘除
		while (matcher.find()) {
			String subFormula = matcher.group();
			String result = "";
			if (subFormula.indexOf('x') != -1) {
				int operatorPosition = subFormula.indexOf('x');
				String num1 = subFormula.substring(0, operatorPosition);
				String num2 = subFormula.substring(operatorPosition+1);
				result = new BigInteger(num1).multiply(new BigInteger(num2)).toString();
			} else if (subFormula.indexOf('/') != -1) {
				int operatorPosition = subFormula.indexOf('/');
				String num1 = subFormula.substring(0, operatorPosition);
				String num2 = subFormula.substring(operatorPosition+1);
				try {
					result = new BigInteger(num1).divide(new BigInteger(num2)).toString();
				} catch(Exception e) {
					return "Divide by 0";
				}
			}
			
			formula = formula.replace(subFormula, result);
			matcher = level1.matcher(formula);
		}

		// 計算加減
		matcher = level2.matcher(formula);
		while(matcher.find()) {
			String subFormula = matcher.group();
			String result = "";
			if (subFormula.indexOf('+') != -1) {
				int operatorPosition = subFormula.indexOf('+');
				String num1 = subFormula.substring(0, operatorPosition);
				String num2 = subFormula.substring(operatorPosition+1);
				result = new BigInteger(num1).add(new BigInteger(num2)).toString();
			} else if (subFormula.indexOf('-') != -1) {
				int operatorPosition = subFormula.lastIndexOf('-');
				String num1 = subFormula.substring(0, operatorPosition);
				String num2 = subFormula.substring(operatorPosition+1);
				result = new BigInteger(num1).subtract(new BigInteger(num2)).toString();
			}
			
			formula = formula.replace(subFormula, result);
			matcher = level2.matcher(formula);
		}
		
		return formula;
	}
	
	
	private boolean isOperator(char ch) {
		return ch == '+' || ch == '-' || ch == 'x' || ch == '/';
	}
	
	/**
	 * 設定按鈕事件和 style
	 */
	private void setupButton(Frame frame) {
		// 設定數字鍵
		for (int i=0; i<numbers.length; i++) {
			numbers[i] = new JButton("" + i);
			setButtonStyle(numbers[i]);
			if (i == 0) {
				numbers[i].setLocation(WIDTH_MARGIN, HEIGHT - HEIGHT/5 + HEIGHT_MARGIN);
				numbers[i].setSize(WIDTH/2, HEIGHT/5);
			} else {
				numbers[i].setLocation((i+2)%3*(WIDTH/4) + WIDTH_MARGIN, HEIGHT - ((i+2)/3+1)*(HEIGHT/5) + HEIGHT_MARGIN);
			}
			numbers[i].addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					String number = e.getActionCommand();
					numberInputEvent(number);
					frame.requestFocus();
				}
			});
			frame.add(numbers[i]);
		}
		
		// 設定運算符號建
		setButtonStyle(plus);
		setOperatorButtonStyle(plus);
		plus.setLocation(3*(WIDTH/4) + WIDTH_MARGIN, 4*(HEIGHT/5) + HEIGHT_MARGIN);
		plus.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				operatorInputEvent("+");
				frame.requestFocus();
			}
		});
		setButtonStyle(minus);
		setOperatorButtonStyle(minus);
		minus.setLocation(3*(WIDTH/4) + WIDTH_MARGIN, 3*(HEIGHT/5) + HEIGHT_MARGIN);
		minus.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				operatorInputEvent("-");
				frame.requestFocus();
			}
		});
		setButtonStyle(multiply);
		setOperatorButtonStyle(multiply);
		multiply.setLocation(3*(WIDTH/4) + WIDTH_MARGIN, 2*(HEIGHT/5) + HEIGHT_MARGIN);
		multiply.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				operatorInputEvent("x");
				frame.requestFocus();
			}
		});
		setButtonStyle(divide);
		setOperatorButtonStyle(divide);
		divide.setLocation(3*(WIDTH/4) + WIDTH_MARGIN, 1*(HEIGHT/5) + HEIGHT_MARGIN);
		divide.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				operatorInputEvent("/");
				frame.requestFocus();
			}
		});
		frame.add(plus);
		frame.add(minus);
		frame.add(multiply);
		frame.add(divide);
		frame.requestFocus();
		
		// 設定等於
		setButtonStyle(equal);
		equal.setBackground(new Color(255, 160, 0));
		equal.setForeground(Color.WHITE);
		equal.setLocation(2*(WIDTH/4) + WIDTH_MARGIN, 4*(HEIGHT/5) + HEIGHT_MARGIN);
		equal.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				equalInputEvent();
				frame.requestFocus();
			}
		});
		equal.removeMouseListener(equal.getMouseListeners()[1]);
		equal.addMouseListener(new MouseAdapter() {
			Color originColor;
			public void mouseEntered(MouseEvent evt) {
				originColor = equal.getBackground();
				equal.setBackground(new Color(190, 80, 0));
			}
		
			public void mouseExited(MouseEvent evt) {
				equal.setBackground(originColor);
			}
		});
		frame.add(equal);
		frame.requestFocus();
		
		// 設定 esc
		setButtonStyle(reset);
		reset.setLocation(WIDTH_MARGIN, HEIGHT_MARGIN);
		reset.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				resetInputEvent();
				frame.requestFocus();
			}
		});
		frame.add(reset);
		frame.requestFocus();
	}
	
	/**
	 * 統一設定按鈕 style
	 */
	private void setButtonStyle(JButton button) {
		button.setSize(WIDTH/4, HEIGHT/5);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBackground(Color.WHITE);
		button.setBorder(new LineBorder(new Color(200, 200, 200), 1, false));
		button.setFont(new Font("Dialog", Font.PLAIN, 30));
		button.addMouseListener(new MouseAdapter() {
			Color originColor;
			public void mouseEntered(MouseEvent evt) {
				originColor = button.getBackground();
				button.setBackground(new Color(200, 200, 200));
			}
		
			public void mouseExited(MouseEvent evt) {
				button.setBackground(originColor);
			}
		});
	}

	/**
	 * 統一設定運算符號按鈕 style
	 */
	private void setOperatorButtonStyle(JButton button) {
		button.setBackground(new Color(240, 240, 240));
		button.setForeground(new Color(255, 160, 0));
	}

}
