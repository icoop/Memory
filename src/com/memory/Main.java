package com.memory;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.TextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import com.memory.PlayGrid.GridSize;

import android.app.Activity;
import android.os.Bundle;


public class Main extends BaseGameActivity{
	
	//States to control the game flow
	public enum States {

		SPLASH,
		MENU,
		OPTIONS,
		GAME,
	}
	
	public States mCurrState = States.SPLASH;
	/* ===================================
	 * CONTANTS
	 * ==================================== */
	private static final int CAMERA_WIDTH=480;
	private static final int CAMERA_HEIGHT=720;
	
	/* =================================
	 * FIELDS
	 * ================================= */
	private Camera mCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mCardBack;
	private PlayGrid mGrid;
	private String set;
	private TextureManager textureManager;

	/* =================================
	 * CONSTRUCTORS
	 * ================================== */


	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera (0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		//Create a new engine by specifying the engine options.
		//For the engine options we are setting full screen, landscape mode, ratio of width/height and using our camera we setup
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		//TODO: Need to get input from user for grid size and set type
		set = "cake_pop";
		mGrid = new PlayGrid(GridSize.MEDIUM, set);
		int numUniqueCards = mGrid.getNumOfCards()/2;
		int bitmapHeight = MathUtils.nextPowerOfTwo(128*(numUniqueCards+1));
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		//Set the size of the bitmap texture (power of 2)and set the quality
		try
		{
			this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128, bitmapHeight, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		}
		catch (IllegalArgumentException error)
		{
			error.printStackTrace();
		}
		//this.mBitmapTextureAtlas = BitmapTextureAtlasFactory.createForTextureAtlasSourceSize(BitmapTextureFormat.RGBA_8888, new AssetBitmapTextureAtlasSource(this, image1), TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		//To load a source regardless of size call BitmapTextureAtlasFactory.createForTextureAtlasSourceSize
		//TextureRegion currTexture = BitmapTextureAtlasTextureRegionFactory.createFromSource(pBitmapTextureAtlas, pBitmapTextureAtlasSource, pTexturePositionX, pTexturePositionY)
		//Setup the cards with the back card graphic
		
	//	this.mCardBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "gfx/"+set+"_back.png", 0, 0);
		this.mCardBack = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, set+"_back.png", 0, 0, (numUniqueCards+1), 1);
		for(int i = 1; i < numUniqueCards+1; i++)
		{
			TiledTextureRegion frontTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, set+"_"+(i)+".png", 0, (int) ((i)*this.mCardBack.getHeight()), (numUniqueCards+1), 1);
			/*BitmapTexture frontTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open("gfx/"+set+"_"+i+".png");
				}
			});*/
			mGrid.addCardToGrid(this.mCardBack, frontTexture);
			
		}
		
		final int centerX = (CAMERA_WIDTH - this.mBitmapTextureAtlas.getWidth()) /2;
		final int centerY = (CAMERA_HEIGHT - this.mBitmapTextureAtlas.getWidth()) /2;
		
		mGrid.initGrid(this.mBitmapTextureAtlas.getWidth());

	//	String image1 = "gfx/ui_ball_1.png";
		//this.mBitmapTextureAtlas = BitmapTextureAtlasFactory.createForTextureAtlasSourceSize(BitmapTextureFormat.RGBA_8888, new AssetBitmapTextureAtlasSource(this, image1), TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		//To load a source regardless of size call BitmapTextureAtlasFactory.createForTextureAtlasSourceSize
	//	this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, image1, 0, 0);
		
		this.mBitmapTextureAtlas.load();
		//mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		//Create an FPSLogger which calls AverageFPSCounter, which uses the FPSCounter updating
		//Default time laps is 5 seconds
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final boolean needsUpdate = false;
		
		//Creates a new scene.  this will be where the data will be displayed.
		//This is a lot like the gba screens.
		final Scene scene = new Scene();
		
		//Create a new blue background
		scene.setColor(0, 0, 0.8784f);
		
		//Do each sprite call the sprite region?
		//createSprite(centerX, centerY);
		//createSpriteSpawnTimer();
		int tapped = 0;
		for(int i = 0; i < mGrid.getNumOfCards(); i++)
		{
			final PlayingCard currCard = mGrid.getCard(i);
			TextureRegion currTexture = currCard.getCardTexture();
			
			Sprite box = new Sprite(currCard.getCardXPosition(),  currCard.getCardYPosition(), currCard.getCardTexture(), getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					mGrid.flipCard(currCard);
					if(currCard.isFlipped())
					{
						
						
					}
				//	boolean flipped = currCard.isFlipped();
				//	boolean matched = currCard.isMatched();
				//	TextureRegion currTexture = currCard.getCardTexture();
				//	this.setTextureRegion(currTexture); 
					return true;
				}
			};
			
			scene.registerTouchArea(box);
			scene.setTouchAreaBindingOnActionDownEnabled(true);
			scene.attachChild(box);
			
			pOnCreateSceneCallback.onCreateSceneFinished(scene);
			
			
		}
		
		scene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(final float pSecondsElapsed) {
            	//Go through and update all the textures
            	mGrid.checkMatchedCards();
                   for(int i = 0; i < mGrid.getNumOfCards(); i++)
                   {
                	   PlayingCard currCard = mGrid.getCard(i);
                	  
                	   Sprite currSprite = (Sprite) scene.getChildByIndex(i);
       
                	 //  currSprite.setTextureRegion(currCard.getCardTexture());
                	  // scene.attachChild(currSprite, i);
                   }
            	
            }

            @Override
            public void reset() {}
    });
		
	/*	scene.registerUpdateHandler(new IUpdateHandler()
		{
			public void onUpdate(float pSecondsElapsed)
			{
				mGrid.checkMatchedCards();
				
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
		*/
	}


	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		// TODO Auto-generated method stub
		
	}

	
	/*@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
				for(int i = 0; i < mGrid.getNumOfCards(); i++)
				{
					PlayingCard currCard = mGrid.getCard(i);
					Sprite currSprite = (Sprite) scene.getChild(i);
					scene.detachChild(currSprite);
					currSprite.setTextureRegion(currCard.getCardTexture());
					scene.attachChild(currSprite);
				}
		// TODO Auto-generated method stub
		return false;
	}*/
	
	
/*	
	
	private void createSprite(float px, float py)
	{
		final Sprite face = new Sprite(px, py, this.mFaceTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				
				setPosition(pSceneTouchEvent.getX() - this.getWidth()/2, pSceneTouchEvent.getY() - this.getHeight()/2);
				return true;
			}
		};
		scene.registerTouchArea(face);
		scene.attachChild(face);
	}
	
	private void createSpriteSpawnTimer()
	{
		final TimerHandler spriteTimerHandler;
		
		this.getEngine().registerUpdateHandler(spriteTimerHandler = new TimerHandler(5, true, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				final float xPos = MathUtils.random(30.0f, (CAMERA_WIDTH - 30.0f));
                final float yPos = MathUtils.random(30.0f, (CAMERA_HEIGHT - 30.0f));
                
                createSprite(xPos, yPos);
			}
		}));
	}*/
}