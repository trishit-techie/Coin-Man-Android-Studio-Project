package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture man[];
	int manState = 0;
	int timeElapsedPositionChanged = 0;
	float gravity = 0.2f;
	float velocity = 0;
	float manPositionY = 0;
	Random random;
	int score = 0;
	BitmapFont font;
	BitmapFont startGame;
	BitmapFont endGame1,endGame2;
	int gameState = 0;
	Texture dizzy;
	int delay = 0;

	ArrayList<Integer> coinPositionX = new ArrayList<Integer>();
	ArrayList<Integer> coinPositionY = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	int timeDelayForNextCoin = 0;
	Texture coin;

	ArrayList<Integer> bombPositionX = new ArrayList<Integer>();
	ArrayList<Integer> bombPositionY = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	int timeDelayForNextBomb = 0;
	Texture bomb;
	Rectangle manRectangle;

	@Override
	public void create() {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manPositionY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
        font.getData().setScale(4);

        dizzy = new Texture("dizzy-1.png");
	}

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinPositionY.add((int) height);
		coinPositionX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombPositionY.add((int) height);
		bombPositionX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render() {
		batch.begin();
		;
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			// GAME IS LIVE

			// FOR BOMBS
			if (timeDelayForNextBomb < 200) {
				timeDelayForNextBomb++;
			} else {
				timeDelayForNextBomb = 0;
				makeBomb();
			}
			bombRectangle.clear();
			for (int i = 0; i < bombPositionX.size(); i++) {
				batch.draw(bomb, bombPositionX.get(i), bombPositionY.get(i));
				bombPositionX.set(i, bombPositionX.get(i) - 6);
				bombRectangle.add(new Rectangle(bombPositionX.get(i), bombPositionY.get(i), bomb.getWidth(), bomb.getHeight()));
			}
			// FOR COINS
			if (timeDelayForNextCoin < 100) {
				timeDelayForNextCoin++;
			} else {
				timeDelayForNextCoin = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for (int i = 0; i < coinPositionX.size(); i++) {
				batch.draw(coin, coinPositionX.get(i), coinPositionY.get(i));
				coinPositionX.set(i, coinPositionX.get(i) - 5);
				coinRectangle.add(new Rectangle(coinPositionX.get(i), coinPositionY.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}
			if (timeElapsedPositionChanged < 2) {
				timeElapsedPositionChanged++;
			} else {
				timeElapsedPositionChanged = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity = velocity + gravity;
			manPositionY = manPositionY - velocity;
			if (manPositionY <= 0) {
				manPositionY = 0;
			}
		}
		else if (gameState==0){
			//WAITING FOR THE GAME TO START
			startGame = new BitmapFont();
			startGame.setColor(Color.WHITE);
			startGame.getData().setScale(4);
			startGame.draw(batch,"Tap to start the game",100,1200);
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}
		else if(gameState == 2){
			// GAME RESET WHEN GAME IS OVER

			endGame1 = new BitmapFont();
			endGame1.setColor(Color.WHITE);
			endGame1.getData().setScale(4);
			endGame1.draw(batch,"Game Over!",200,1200);
			endGame2 = new BitmapFont();
			endGame2.setColor(Color.WHITE);
			endGame2.getData().setScale(4);
			endGame2.draw(batch,"Tap to play again",150,1050);

			if(Gdx.input.justTouched()){
				gameState = 1;
				manPositionY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinPositionX.clear();
				coinPositionY.clear();
				coinRectangle.clear();
				timeDelayForNextCoin = 0;
				bombPositionX.clear();
				bombPositionY.clear();
				bombRectangle.clear();
				timeDelayForNextBomb = 0;
			}
		}

		if(gameState == 2){
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manPositionY,300,400);
		}
		else{
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manPositionY,300,400);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, (int) manPositionY, man[manState].getWidth(), man[manState].getHeight());

		for (int i = 0; i < coinRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
				Gdx.app.log("info", "Collision occured with a COIN!");
				score++;
				coinRectangle.remove(i);
				coinPositionX.remove(i);
				coinPositionY.remove(i);
				break;
			}
		}

		for (int i = 0; i < bombRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
				Gdx.app.log("info", "Collision occured with a BOMB!");
				gameState=2;
			}
		}

		font.draw(batch,"Score: "+score,50,1350);

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();

	}
}

