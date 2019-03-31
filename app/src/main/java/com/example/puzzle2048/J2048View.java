package com.example.puzzle2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class J2048View extends View {
    final int DIR_LEFT = 0;
    final int DIR_RIGHT = 1;
    final int DIR_TOP = 2;
    final int DIR_BOTTOM = 3;
    boolean mFirstDraw = true;
    int mCountX = 4, mCountY = 4;
    int mScrWidth = 480, mScrHeight = 800;
    float mScrPartW = 120.f, mScrPartH = 266.f;
    float mMargin = 10.f, mPadding = 10.f;
    Random mRand = new Random();
    Point mTouchPrev = new Point(-1,-1);
    int[][] mMatrix = new int[mCountY][mCountX];
    boolean mReadData = false;

    // 생성자 함수
    public J2048View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public J2048View(Context context,
                     int blockCountX, int blockCountY) {
        super(context);
        mCountX = blockCountX;
        mCountY = blockCountY;
    }

    public void setInfo(int blockCount) {
        mCountX = mCountY = blockCount;
    }
    public void setInfo(int blockCountX, int blockCountY) {
        mCountX = blockCountX;
        mCountY = blockCountY;
    }

    // Value 를 Property 에 저장
    public void saveData() {
        for(int j=0; j < mCountY; j++) {
            for(int i=0; i < mCountX; i++) {
                String strKey = String.format("%d-%d", j, i);
                System.setProperty(strKey, Integer.toString(mMatrix[j][i]));
            }
        }
    }

    // Value 를 Property 에서 읽는다
    public void readData() {
        for(int j=0; j < mCountY; j++) {
            for(int i=0; i < mCountX; i++) {
                String strKey = String.format("%d-%d", j, i);
                String strVal = System.getProperty(strKey, "0");
                mMatrix[j][i] = Integer.parseInt(strVal);
                if( mMatrix[j][i] > 0 )
                    mReadData = true;
            }
        }
    }

    public void init(Canvas canvas) {
        // 화면 크기와 부분 이미지의 화면 출력 크기를 계산
        mScrWidth = canvas.getWidth();
        mScrHeight = canvas.getHeight();
        mMargin = mScrWidth / 37.f;
        mPadding = mScrWidth / 47.f;
        float edgeLength = mMargin * 2 + mPadding * (mCountX - 1);
        mScrPartW = (float)(mScrWidth - edgeLength) / mCountX;
        edgeLength = mMargin * 2 + mPadding * (mCountY - 1);
        mScrPartH = (float)(mScrHeight - edgeLength) / mCountY;
        //mPref = getSharedPreferences("data", MODE_PRIVATE);

        // Property 에서 데이터를 읽지 않았다면
        if( mReadData == false)
            // 게임을 재시작
            restartGame();
    }

    // 게임을 재시작
    public void restartGame() {
        // Value 초기화
        for(int j=0; j < mCountY; j++) {
            for(int i=0; i < mCountX; i++) {
                mMatrix[j][i] = 0;
            }
        }
        // 새로운 Value 를 추가
        addNewValue(true);
        addNewValue(true);
        this.invalidate();
    }

    // 새로운 Value 를 추가
    public boolean addNewValue(boolean fix) {
        // 랜덤으로 빈방을 찾아서 방번호 반환
        Point pt = findEmptyRandom();
        if( pt == null )
            return false;

        int value = 2;
        if( fix == false ) {
            int rand = getRandom(10, 0);
            if( rand == 4 )
                value = 4;
        }
        mMatrix[pt.y][pt.x] = value;
        this.invalidate();
        return true;
    }

    // 랜덤으로 빈방을 찾아서 방번호 반환
    public Point findEmptyRandom() {
        // 모든 방이 채워졌다면 null 을 반환
        if( isGameOver() )
            return null;

        int x = 0, y = 0;
        Point pt = new Point();
        do {
            pt.x = getRandom(mCountX, 0);
            pt.y = getRandom(mCountY, 0);
        } while(mMatrix[pt.y][pt.x] != 0);
        return pt;
    }

    // 모든 방이 채워졌는지 체크
    public boolean isGameOver() {
        for(int j=0; j < mCountY; j++) {
            for(int i=0; i < mCountX; i++) {
                if( mMatrix[j][i] == 0 )
                    return false;
            }
        }
        return true;
    }

    public int getRandom(int max, int offset) {
        int nResult = mRand.nextInt(max) + offset;
        return nResult;
    }

    // 블록 번호에 해당하는 영역좌표를 반환
    public RectF getBlockRect(int x, int y) {
        RectF rect = new RectF();
        float left = (float)(mMargin + x * (mScrPartW + mPadding));
        float top = mMargin + y * (mScrPartH + mPadding);
        rect.left = left;
        rect.top = top;
        rect.right = left + (int)mScrPartW;
        rect.bottom = top + (int)mScrPartH;
        return rect;
    }

    // 캔버스에 그림을 그리는 함수
    public void onDraw(Canvas canvas) {
        if( mFirstDraw ) {
            mFirstDraw = false;
            init(canvas);
        }
        canvas.drawColor(Color.rgb(186, 171, 157));
        // 블록을 캔버스에 그린다
        drawBlock(canvas);
    }

    // 블록을 캔버스에 그린다
    public void drawBlock(Canvas canvas) {
        Paint pnt = new Paint();
        pnt.setStyle(Paint.Style.FILL);
        pnt.setAntiAlias(true);
        pnt.setTextAlign(Paint.Align.CENTER);
        pnt.setTypeface(Typeface.create((String) null, Typeface.BOLD));

        int[] crBack = {Color.rgb(203,191,178), Color.rgb(236, 228, 217),
                Color.rgb(236,224,199), Color.rgb(242, 177, 121),
                Color.rgb(244,148,99), Color.rgb(245,124,96),
                Color.rgb(234,89,55), Color.rgb(243,216,107),
                Color.rgb(238,206,97), Color.rgb(239,203,81)};
        int[] crNum = {Color.argb(0,0,0,0), Color.rgb(119,110,101),
                Color.rgb(119, 110, 101), Color.rgb(255, 255, 254)};

        for(int j=0; j < mCountY; j++) {
            for(int i=0; i < mCountX; i++) {
                // 블록 번호에 해당하는 영역좌표를 반환
                RectF rect = getBlockRect(i, j);
                // Value 가 2의 몇승인지를 반환
                int index = valueIndex(mMatrix[j][i]);
                //int backIndex = index;

                // 블록 배경을 그린다
                //if( index >= crBack.length )
                //    backIndex = crBack.length - 1;
                int backIndex = ( index >= crBack.length ) ? crBack.length - 1 : index;
                pnt.setColor(crBack[backIndex]);
                canvas.drawRoundRect(rect, mPadding, mPadding, pnt);

                // 블록 숫자를 출력
                int numIndex = ( index >= crNum.length ) ? crNum.length - 1 : index;
                // Value 길이에 따라서 텍스트 크기를 반환
                int textSize = getTextSize(mMatrix[j][i]);
                pnt.setTextSize(textSize);
                pnt.setColor(crNum[numIndex]);
                int textY = (int)(rect.centerY() + (double)textSize/2.4);
                canvas.drawText(Integer.toString(mMatrix[j][i]),
                        rect.centerX(), textY, pnt);
            }
        }
    }

    // Value 길이에 따라서 텍스트 크기를 반환
    public int getTextSize(int value) {
        int size = 20;
        if( value > 1000 ) {
            size = (int)(mScrPartW / 2.6);
            return size;
        }
        if( value > 100 ) {
            size = (int)(mScrPartW / 2);
            return size;
        }
        size = (int)(mScrPartW / 1.8);
        return size;
    }

    // Value 가 2의 몇승인지를 반환
    public int valueIndex(int value) {
        if( value < 2 )
            return 0;
        int index = 0;
        do {
            value /= 2;
            index ++;
        } while(value > 1);
        return index;
    }

    // 터치 이벤트 함수
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // 터치 포인트 위치좌표를 구한다
        int x1 = (int) event.getX();
        int y1 = (int) event.getY();
        // 터치 이벤트 처리
        touchProcess(x1, y1, event.getAction());

        return true;
    }

    // 터치 이벤트 처리
    public void touchProcess(int x1, int y1, int action) {

        switch (action) {
            // 터치 다운 이벤트 일때
            case MotionEvent.ACTION_DOWN:
                mTouchPrev.x = x1;
                mTouchPrev.y = y1;
                break;
            // 터치 드래그 이벤트 일때
            case MotionEvent.ACTION_MOVE :
                if( mTouchPrev.x < 0 || mTouchPrev.y < 0 )
                    return;
                // 오른쪽 드래그
                if( x1 - mTouchPrev.x > mScrPartW ) {
                    // 슬라이드 이동한 다음 새로운 Value 추가
                    slideMoveAndAddNew(DIR_RIGHT);
                    // 슬라이드 이동
                    //slideMove(DIR_RIGHT);
                    // 새로운 Value 를 추가
                    //addNewValue(false);
                }
                // 왼쪽 드래그
                else if( mTouchPrev.x - x1 > mScrPartW ) {
                    // 슬라이드 이동한 다음 새로운 Value 추가
                    slideMoveAndAddNew(DIR_LEFT);
                    // 슬라이드 이동
                    //slideMove(DIR_LEFT);
                    // 새로운 Value 를 추가
                    //addNewValue(false);
                }
                // 아래쪽 드래그
                else if( y1 - mTouchPrev.y > mScrPartH ) {
                    // 슬라이드 이동한 다음 새로운 Value 추가
                    slideMoveAndAddNew(DIR_BOTTOM);
                    // 슬라이드 이동
                    //slideMove(DIR_BOTTOM);
                    // 새로운 Value 를 추가
                    //addNewValue(false);
                }
                // 위쪽 드래그
                else if( mTouchPrev.y - y1 > mScrPartH ) {
                    // 슬라이드 이동한 다음 새로운 Value 추가
                    slideMoveAndAddNew(DIR_TOP);
                    // 슬라이드 이동
                    //slideMove(DIR_TOP);
                    // 새로운 Value 를 추가
                    //addNewValue(false);
                }

                break;
            // 터치 해제 이벤트 일때
            case MotionEvent.ACTION_UP :
                mTouchPrev.x = -1;
                mTouchPrev.y = -1;
                break;
        }
    }

    // 슬라이드 이동한 다음 새로운 Value 추가
    public void slideMoveAndAddNew(int dir) {
        // 슬라이드 이동. 이동한 Value 가 없다면 함수 탈출
        if( slideMove(dir) == false )
            return;
        // 새로운 Value 를 추가
        addNewValue(false);
        mTouchPrev.x = -1;
        mTouchPrev.y = -1;
    }

    // 위쪽 방향으로 같은 Value 2개가 붙어있는지 찾는다
    public Point findSame2Value(int[][] mat, int indexX) {
        Point pt = new Point();
        // 아래쪽에서 1번째와 2번째 Value 를 찾는다
        int first=-1, second=-1;
        for(int j = mCountY-1; j >= 0; j--) {
            if( mat[j][indexX] != 0 ) {
                if( first < 0 ) {
                    first = j;
                    continue;
                }
                else if (mat[first][indexX] == mat[j][indexX]) {
                    pt.x = first;
                    pt.y = j;
                    return pt;
                }
                first = j;
            }
        }
        return null;
    }

    // 슬라이드 이동
    public boolean slideMove(int dir) {
        // 아래쪽 방향으로 Matrix 를 회전
        int[][] matTemp = new int[mCountY][mCountX];
        rotateMatrix(mMatrix, matTemp, dir, false);

        boolean bMove = false;
        for(int i=0; i < mCountX; i++) {
            // 위쪽 방향으로 같은 Value 2개가 붙어있는지 찾는다
            Point pt = findSame2Value(matTemp, i);

            // 아래쪽 Value 2개가 값은 값이라면 합친다
            if(pt != null && pt.x >= 0 && pt.y >= 0
                    && (matTemp[pt.x][i] == matTemp[pt.y][i])) {
                matTemp[pt.x][i] *= 2;
                matTemp[pt.y][i] = 0;
                bMove = true;
            }

            // Value 를 아래쪽으로 이동해서 쌓는다
            for(int j = mCountY-1; j >= 0; j--) {
                if( matTemp[j][i] <= 0 )
                    continue;
                // Value 를 찾았다면
                int val = matTemp[j][i];
                matTemp[j][i] = 0;
                // 아래쪽 부터 위로 찾으면서 빈칸에 Value 를 채운다
                for(int k = mCountY-1; k >= 0; k--) {
                    if( matTemp[k][i] == 0 ) {
                        matTemp[k][i] = val;
                        // Value 를 이동했다면
                        if( j != k )
                            bMove = true;
                        break;
                    }
                }
            }
        }

        // 원래대로 Matrix 를 회전
        rotateMatrix(matTemp, mMatrix, dir, true);
        return bMove;
    }

    // 배열을 방향에 따라서 아래쪽 방향으로 회전
    public void rotateMatrix(int[][] orig, int[][] dest, int dir,
                             boolean recover) {
        //int[][] matTemp = new int[mCountY][mCountX];

        switch (dir) {
            // 아래쪽 방향일때 그대로 복사
            case DIR_BOTTOM :
                for(int j=0; j < mCountY; j++) {
                    for(int i=0; i < mCountX; i++) {
                        dest[j][i] = orig[j][i];
                    }
                }
                break;
            // 위쪽 방향일때 위아래를 반대로 복사
            case DIR_TOP :
                for(int j=0; j < mCountY; j++) {
                    for(int i=0; i < mCountX; i++) {
                        dest[mCountY - j - 1][i] = orig[j][i];
                    }
                }
                break;
            // 오른쪽 방향일때 시계방향 90도 회전해서 복사
            case DIR_RIGHT :
                for(int j=0; j < mCountY; j++) {
                    for(int i=0; i < mCountX; i++) {
                        dest[i][j] = orig[j][i];
                    }
                }
                break;
            // 왼쪽 방향일때 반시계방향 90도 회전해서 복사
            case DIR_LEFT :
                for(int j=0; j < mCountY; j++) {
                    for(int i=0; i < mCountX; i++) {
                        // 원래 상태로 되돌릴때
                        if( recover )
                            dest[mCountY - i - 1][j] = orig[mCountX - j - 1][i];
                            // 회전할때
                        else
                            dest[mCountX - i - 1][j] = orig[mCountX - j - 1][i];
                    }
                }
                break;
        }

        //return matTemp;
    }

    // 이벤트 리스너 클래스 정의
    public interface EventListener {
        void onCompleted();
    }

    // 이벤트 리스너를 멤버 변수로 선언
    private EventListener mEventListener = null;

    // 이벤트 리스너 객체를 받아서 멤버변수에 저장하는 함수
    public void setListener(EventListener listener){
        mEventListener = listener;
    }

}
