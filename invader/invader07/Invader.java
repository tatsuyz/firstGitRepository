import java.awt.Container;

import javax.swing.JFrame;
/*
 * Created on 2005/02/09
 *
 */

/**
 * �C���x�[�_�[�Q�[���{��
 * 
 * @author mori
 *  
 */
public class Invader extends JFrame {
    public Invader() {
        // �^�C�g����ݒ�
        setTitle("���ʉ��̓���");
        // �T�C�Y�ύX�s��
        setResizable(false);

        // ���C���p�l�����쐬���ăt���[���ɒǉ�
        MainPanel panel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        // �p�l���T�C�Y�ɍ��킹�ăt���[���T�C�Y�������ݒ�
        pack();
    }

    public static void main(String[] args) {
        Invader frame = new Invader();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}