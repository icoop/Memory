package com.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public class PlayGrid {

	//The size of the grid
	public static enum GridSize { SMALL, MEDIUM, LARGE };
	static final private int SMALL_WIDTH = 2;
	static final private int SMALL_HEIGHT = 3;
	static final private int MEDIUM_WIDTH = 3;
	static final private int MEDIUM_HEIGHT = 4;
	static final private int LARGE_WIDTH = 6;
	static final private int LARGE_HEIGHT = 6;
	static final private float PADDING = 5.0f;
	
	private List<PlayingCard> mCards;
	private int mWidth;
	private int mHeight;
	private int mNumOfCards;
	private String mSet;
	private int mNumFlipped;
	private float startX;
	private float startY;
	
	//Pass in the size of the grid and the card set to use
	public PlayGrid(GridSize size, String set)
	{
		//Setup the size of the grid
		setGridSize(size);
		this.mSet = set;
		mCards = new ArrayList();
		this.mNumFlipped = 0;	
		
	}
	
	public void initGrid(float cardWidth)
	{		
		//Randomize the cards
		Collections.shuffle(mCards, new Random(System.currentTimeMillis()));
		
		//Go through all the cards now and add their positions
		Iterator<PlayingCard> cardIterator = mCards.iterator();
		
		
		
		for(int h = 0; h < mHeight; h++)
		{
			for(int w = 0; w < mWidth; w++)
			{
				//Get the next card in the list
				PlayingCard currCard = cardIterator.next();
				float x = this.startX+(w*cardWidth)+(PADDING*w);
				float y = this.startY+(h*cardWidth)+(PADDING*h);
				//Set the position based off the current grid location and the starting grid location
				currCard.setCardPosition(x, y);
			}
		}
	}
	
	//Add a set of cards to the grid since we'll always need a pair
	public void addCardToGrid(TextureRegion mCardBack, TextureRegion frontTexture)
	{
		int currIndex = 0;
		if(mCards != null)
		{
			currIndex = mCards.size()/2;
		}
		PlayingCard currCard = new PlayingCard(currIndex, mCardBack, frontTexture);
		PlayingCard currCard2 = new PlayingCard(currIndex, mCardBack, frontTexture);
		mCards.add(currCard);
		mCards.add(currCard2);
		
	}
	
	public void setGridSize(GridSize size)
	{
		switch (size)
		{
		case SMALL:
			this.mWidth = SMALL_WIDTH;
			this.mHeight = SMALL_HEIGHT;
			break;
		case MEDIUM:
			this.mWidth = MEDIUM_WIDTH;
			this.mHeight = MEDIUM_HEIGHT;
			this.startX = 45;
			this.startY = 102;
			break;
		case LARGE:
			this.mWidth = LARGE_WIDTH;
			this.mHeight = LARGE_HEIGHT;
			break;
		default:
			this.mWidth = 3;
			this.mHeight = 4;
			break;
		}
		
		this.mNumOfCards = this.mHeight*this.mWidth;
		
	}
	
	public int getGridHeight()
	{
		return this.mHeight;
	}
	
	public int getGridWidth()
	{
		return this.mWidth;
	}
	
	public int getNumOfCards()
	{
		return this.mNumOfCards;
	}
	
	public PlayingCard getCard(int index)
	{
		return mCards.get(index);
	}
	
	
	public void flipCard(PlayingCard card)
	{
		//Flip the card if it isn't flipped, matched and we have less than 2 flipped
		if(!card.isFlipped() && !card.isMatched() && this.mNumFlipped <= 2)
		{
			card.setFlipped(true);		
			this.mNumFlipped++;
		}
		
	}
	
	//Check to see if the cards match.  If they don't then unflip them.  If they match set them to matched
	public void checkMatchedCards()
	{		
		//We need 2 cards flipped to make a match
		if(this.mNumFlipped == 2)
		{		
			int currId = -1;
			int firstIndex = -1;
			int secondIndex = -1;
			
			//Go through the cards and see which ones are flipped
			for(int i = 0; i < this.mNumOfCards; i++)
			{
				PlayingCard currCard = mCards.get(i);
				if(currCard.isFlipped())
				{
					if(firstIndex < 0)
					{
						firstIndex = i;
					}
					else
					{
						secondIndex = i;
					}
				}
			}
			
			if(firstIndex >= 0 && secondIndex >= 0)
			{
				PlayingCard card1 = mCards.get(firstIndex);
				PlayingCard card2 = mCards.get(secondIndex);
				
				if(card1.getCardId() == card2.getCardId())
				{
					card1.setMatched(true);
					card2.setMatched(true);
				}
				
				card1.setFlipped(false);
				card2.setFlipped(false);
				this.mNumFlipped = 0;
			}
			
		}
	}
}
