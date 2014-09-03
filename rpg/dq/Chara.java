import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * Created on 2005/12/25
 *
 */

/**
 * @author mori
 *
 */
public class Chara {
    // �ړ��X�s�[�h
    private static final int SPEED = 4;

    // �ړ��m��
    public static final double PROB_MOVE = 0.02;

    // �C���[�W
    private static BufferedImage charaImage;

    // �L�����N�^�[�ԍ�
    private int charaNo;

    // ���W
    private int x, y;   // �P�ʁF�}�X
    private int px, py; // �P�ʁF�s�N�Z��

    // �����Ă�������iLEFT,RIGHT,UP,DOWN�̂ǂꂩ�j
    private int direction;
    // �A�j���[�V�����J�E���^
    private int count;
    
    //  �ړ����i�X�N���[�����j��
    private boolean isMoving;
    //  �ړ����̏ꍇ�̈ړ��s�N�Z����
    private int movingLength;

    // �ړ����@
    private int moveType;
    // �͂Ȃ����b�Z�[�W
    private String message;

    // �A�j���[�V�����p�X���b�h
    private Thread threadAnime;
    
    // �}�b�v�ւ̎Q��
    private Map map;

    public Chara(int x, int y, int charaNo, int direction, int moveType, Map map) {
        this.x = x;
        this.y = y;

        px = x * Chipset.SIZE;
        py = y * Chipset.SIZE;

        this.charaNo = charaNo;
        this.direction = direction;
        count = 0;
        this.moveType = moveType;
        this.map = map;

        // ����̌Ăяo���̂݃C���[�W�����[�h
        if (charaImage == null) {
            loadImage();
        }

        // �L�����N�^�[�A�j���[�V�����p�X���b�h�J�n
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        int cx = (charaNo % 8) * (Chipset.SIZE * 2);
        int cy = (charaNo / 8) * (Chipset.SIZE * 4);
        // count��direction�̒l�ɉ����ĕ\������摜��؂�ւ���
        g.drawImage(charaImage, px + offsetX, py + offsetY, px + offsetX + Chipset.SIZE, py + offsetY + Chipset.SIZE,
            cx + count * Chipset.SIZE, cy + direction * Chipset.SIZE, cx + Chipset.SIZE + count * Chipset.SIZE, cy + direction * Chipset.SIZE + Chipset.SIZE, null);
    }

    /**
     * �ړ������B 
     * @return 1�}�X�ړ�������������true��Ԃ��B�ړ�����false��Ԃ��B
     */
    public boolean move() {
        switch (direction) {
            case Direction.LEFT:
                if (moveLeft()) {
                    // �ړ�����������
                    return true;
                }
                break;
            case Direction.RIGHT:
                if (moveRight()) {
                    // �ړ�����������
                    return true;
                }
                break;
            case Direction.UP:
                if (moveUp()) {
                    // �ړ�����������
                    return true;
                }
                break;
            case Direction.DOWN:
                if (moveDown()) {
                    // �ړ�����������
                    return true;
                }
                break;
        }
        
        // �ړ����������Ă��Ȃ�
        return false;
    }

    /**
     * ���ֈړ�����B
     * @return 1�}�X�ړ�������������true��Ԃ��B�ړ�����false��Ԃ��B
     */
    private boolean moveLeft() {
        // 1�}�X��̍��W
        int nextX = x - 1;
        int nextY = y;
        if (nextX < 0) {
            nextX = 0;
        }
        // ���̏ꏊ�ɏ�Q�����Ȃ���Έړ����J�n
        if (!map.isHit(nextX, nextY)) {
            // SPEED�s�N�Z�����ړ�
            px -= Chara.SPEED;
            if (px < 0) {
                px = 0;
            }
            // �ړ����������Z
            movingLength += Chara.SPEED;
            // �ړ���1�}�X���𒴂��Ă�����
            if (movingLength >= Chipset.SIZE) {
                // �ړ�����
                x--;
                px = x * Chipset.SIZE;
                // �ړ�������
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            // ���̈ʒu�ɖ߂�
            px = x * Chipset.SIZE;
            py = y * Chipset.SIZE;
        }
        
        return false;
    }

    /**
     * �E�ֈړ�����B
     * @return 1�}�X�ړ�������������true��Ԃ��B�ړ�����false��Ԃ��B
     */
    private boolean moveRight() {
        // 1�}�X��̍��W
        int nextX = x + 1;
        int nextY = y;
        if (nextX > map.getCol() - 1) {
            nextX = map.getCol() - 1;
        }
        // ���̏ꏊ�ɏ�Q�����Ȃ���Έړ����J�n
        if (!map.isHit(nextX, nextY)) {
            // SPEED�s�N�Z�����ړ�
            px += Chara.SPEED;
            if (px > map.getWidth() - Chipset.SIZE) {
                px = map.getWidth() - Chipset.SIZE;
            }
            // �ړ����������Z
            movingLength += Chara.SPEED;
            // �ړ���1�}�X���𒴂��Ă�����
            if (movingLength >= Chipset.SIZE) {
                // �ړ�����
                x++;
                px = x * Chipset.SIZE;
                // �ړ�������
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * Chipset.SIZE;
            py = y * Chipset.SIZE;
        }
        
        return false;
    }

    /**
     * ��ֈړ�����B
     * @return 1�}�X�ړ�������������true��Ԃ��B�ړ�����false��Ԃ��B
     */
    private boolean moveUp() {
        // 1�}�X��̍��W
        int nextX = x;
        int nextY = y - 1;
        if (nextY < 0) {
            nextY = 0;
        }
        // ���̏ꏊ�ɏ�Q�����Ȃ���Έړ����J�n
        if (!map.isHit(nextX, nextY)) {
            // SPEED�s�N�Z�����ړ�
            py -= Chara.SPEED;
            if (py < 0) py = 0;
            // �ړ����������Z
            movingLength += Chara.SPEED;
            // �ړ���1�}�X���𒴂��Ă�����
            if (movingLength >= Chipset.SIZE) {
                // �ړ�����
                y--;
                py = y * Chipset.SIZE;
                // �ړ�������
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * Chipset.SIZE;
            py = y * Chipset.SIZE;
        }
        
        return false;
    }

    /**
     * ���ֈړ�����B
     * @return 1�}�X�ړ�������������true��Ԃ��B�ړ�����false��Ԃ��B
     */
    private boolean moveDown() {
        // 1�}�X��̍��W
        int nextX = x;
        int nextY = y + 1;
        if (nextY > map.getRow() - 1) {
            nextY = map.getRow() - 1;
        }
        // ���̏ꏊ�ɏ�Q�����Ȃ���Έړ����J�n
        if (!map.isHit(nextX, nextY)) {
            // SPEED�s�N�Z�����ړ�
            py += Chara.SPEED;
            if (py > map.getHeight() - Chipset.SIZE) {
                py = map.getHeight() - Chipset.SIZE;
            }
            // �ړ����������Z
            movingLength += Chara.SPEED;
            // �ړ���1�}�X���𒴂��Ă�����
            if (movingLength >= Chipset.SIZE) {
                // �ړ�����
                y++;
                py = y * Chipset.SIZE;
                // �ړ�������
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * Chipset.SIZE;
            py = y * Chipset.SIZE;
        }
        
        return false;
    }

    /**
     * �L�����N�^�[�������Ă�������̂ƂȂ�ɃL�����N�^�[�����邩���ׂ�
     * @return �L�����N�^�[�������炻��Chara�I�u�W�F�N�g��Ԃ�
     */
    public Chara talkWith() {
        int nextX = 0;
        int nextY = 0;
        // �L�����N�^�[�̌����Ă��������1���ƂȂ�̍��W
        switch (direction) {
            case Direction.LEFT:
                nextX = x - 1;
                nextY = y;
                // �����ɑ䂪����΂���ɂ��̐�ɃZ�b�g
                // ��z���ɘb����悤�ɂ���
                if (map.getMapChip(nextX, nextY) == Chipset.THRONE) {
                    nextX--;
                }
                break;
            case Direction.RIGHT:
                nextX = x + 1;
                nextY = y;
                if (map.getMapChip(nextX, nextY) == Chipset.THRONE) {
                    nextX++;
                }
                break;
            case Direction.UP:
                nextX = x;
                nextY = y - 1;
                if (map.getMapChip(nextX, nextY) == Chipset.THRONE) {
                    nextY--;
                }
                break;
            case Direction.DOWN:
                nextX = x;
                nextY = y + 1;
                if (map.getMapChip(nextX, nextY) == Chipset.THRONE) {
                    nextY++;
                }
                break;
        }
        // ���̕����ɃL�����N�^�[�����邩���ׂ�
        Chara chara;
        chara = map.charaCheck(nextX, nextY);
        // �L�����N�^�[������Θb���������L�����N�^�[�̕��֌�����
        if (chara != null) {
            switch (direction) {
                case Direction.LEFT:
                    chara.setDirection(Direction.RIGHT);
                    break;
                case Direction.RIGHT:
                    chara.setDirection(Direction.LEFT);
                    break;
                case Direction.UP:
                    chara.setDirection(Direction.DOWN);
                    break;
                case Direction.DOWN:
                    chara.setDirection(Direction.UP);
                    break;
            }
        }
        
        return chara;
    }

    /**
     * �������Ƃ��ڂ̑O�ɉ������邩���ׂ�
     * @return �C�x���g�I�u�W�F�N�g
     */
    public Event search() {
        // �����𒲂ׂ�
        Event event = map.eventCheck(x, y);
        if (event != null) {
            return event;
        }
        
        // �ڂ̑O�𒲂ׂ�
        int nextX = 0;
        int nextY = 0;
        switch (direction) {
            case Direction.LEFT:
                nextX = x - 1;
                nextY = y;
                break;
            case Direction.RIGHT:
                nextX = x + 1;
                nextY = y;
                break;
            case Direction.UP:
                nextX = x;
                nextY = y - 1;
                break;
            case Direction.DOWN:
                nextX = x;
                nextY = y + 1;
                break;
        }

        event = map.eventCheck(nextX, nextY);
       if (event != null) {
           return event;
       }
        
        return null;
    }

    /**
     * �ڂ̑O�Ƀh�A�����邩���ׂ�
     * @return �ڂ̑O�ɂ���DoorEvent�I�u�W�F�N�g
     */
    public DoorEvent open() {
        int nextX = 0;
        int nextY = 0;
        // �L�����N�^�[�̌����Ă��������1���ƂȂ�̍��W
        switch (direction) {
            case Direction.LEFT:
                nextX = x - 1;
                nextY = y;
                break;
            case Direction.RIGHT:
                nextX = x + 1;
                nextY = y;
                break;
            case Direction.UP:
                nextX = x;
                nextY = y - 1;
                break;
            case Direction.DOWN:
                nextX = x;
                nextY = y + 1;
                break;
        }
        // ���̕����Ƀh�A�����邩���ׂ�
        Event event = map.eventCheck(nextX, nextY);
        if (event instanceof DoorEvent) {
            return (DoorEvent)event;
        }
        
        return null;
    }

    private void loadImage() {
        // �L�����N�^�[�̃C���[�W�����[�h
        try {
            charaImage = ImageIO.read(getClass().getResource("image/chara.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getPx() {
        return px;
    }
    
    public int getPy() {
        return py;
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean flag) {
        isMoving = flag;
        // �ړ�������������
        movingLength = 0;
    }

    /**
     * �L�����N�^�[�̃��b�Z�[�W��Ԃ�
     * @return ���b�Z�[�W
     */
    public String getMessage() {
        return message;
    }

    /**
     * �L�����N�^�[�̃��b�Z�[�W��Ԃ�
     * @param message ���b�Z�[�W
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public int getMoveType() {
        return moveType;
    }

    // �A�j���[�V�����N���X
    private class AnimationThread extends Thread {
        public void run() {
            while (true) {
                // count��؂�ւ���
                if (count == 0) {
                    count = 1;
                } else if (count == 1) {
                    count = 0;
                }
                
                // 300�~���b�x�~��300�~���b�����ɃL�����N�^�[�̊G��؂�ւ���
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } 
            }
        }
    }
}