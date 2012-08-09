package com.memory;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public class PlayingCard {

	// *** VARIABLES *** //
	private boolean mIsFlipped;
	private boolean mIsMatched;
	
	private TextureRegion mCardFrontTexture;
	private TextureRegion mCardBackTexture;
	private float mXPos;
	private float mYPos;
	
	private int mId;
	
	// *** METHODS *** //
	public PlayingCard(int Id, float x, float y, TextureRegion mCardBack, TextureRegion frontTexture)
	{
		this.mCardBackTexture= mCardBack;
		this.mCardFrontTexture = frontTexture;
		this.mId = Id;
		this.mXPos = x;
		this.mYPos = y;
		this.mIsFlipped = false;
		this.mIsMatched = false;
	}
	
	public PlayingCard(int Id, TextureRegion mCardBack, TextureRegion frontTexture)
	{
		this(Id, 0, 0, mCardBack, frontTexture);
	}
	
	public void setCardTexture(TextureRegion texture){
		this.mCardFrontTexture = texture;
	}
	
	/**
	 * Gets the current texture for the sprite.  If it's flipped or match use the front card.  Else use the back
	 * @return The current TextureRegion for the sprite
	 */
	public TextureRegion getCardTexture()
	{
		TextureRegion returnTexture = this.mCardBackTexture;
		
		if(isMatched() || isFlipped())
		{
			returnTexture = this.mCardFrontTexture;
		}
		
		return returnTexture;
	}
	
	public void setCardId(int id)
	{
		this.mId = id;
	}
	public int getCardId()
	{
		return this.mId;
	}
	public void setFlipped(boolean isFlipped) {
		this.mIsFlipped = isFlipped;
	}
	public boolean isFlipped() {
		return mIsFlipped;
	}
	public void setMatched(boolean isMatched) {
		this.mIsMatched = isMatched;
	}
	public boolean isMatched() {
		return mIsMatched;
	}
	public void setCardPosition(float x, float y)
	{
		this.mXPos = x;
		this.mYPos = y;
	}
	
	public float getCardXPosition()
	{
		return this.mXPos;
	}
	
	public float getCardYPosition()
	{
		return this.mYPos;
	}
	
}
