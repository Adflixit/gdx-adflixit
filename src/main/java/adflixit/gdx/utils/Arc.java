package adflixit.gdx.utils;

import static com.badlogic.gdx.utils.Align.*;
import static adflixit.gdx.BaseGame.*;
import static adflixit.gdx.BaseContext.*;
import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Widget that draws an arc aka radial progress bar.
 */
public class Arc extends Widget {
  private Drawable            drawable;
  private ShapeRenderer       shr;
  private final MutableFloat  progress  = new MutableFloat(0);
  private float               radius;
  private boolean             clockwise = true;

  public Arc(Drawable drawable, float radius, ShapeRenderer shprnd) {
    setDrawable(drawable);
    setRadius(radius);
    setSize(getPrefWidth(), getPrefHeight());
    setOrigin(center);
    shr = shprnd;
  }

  public Arc(String drawable, float radius, ShapeRenderer shprnd) {
    this(drawable(drawable), radius, shprnd);
  }

  public Arc(float radius, ShapeRenderer shprnd) {
    this("arc", radius, shprnd);
  }

  public Arc(ShapeRenderer shprnd) {
    this(200, shprnd);
  }

  public Arc setDrawable(Drawable drawable) {
    this.drawable = drawable;
    return this;
  }

  public Arc setRadius(float radius) {
    this.radius = radius;
    return this;
  }

  public Arc setClockwise(boolean value) {
    clockwise = value;
    return this;
  }

  public float progress() {
    return progress.floatValue();
  }

  public float degrees() {
    return progress() * 360;
  }

  public void set(float value) {
    killTweenTarget(progress);
    progress.setValue(value);
  }

  public void reset() {
    set(0);
  }

  /**
   * @param v value
   * @param d duration
   * @param eq equation
   */
  public void tween(float v, float d, TweenEquation eq) {
    $tween(v, d, eq).start(uiTweenMgr);
  }

  /**
   * @param v value
   * @param d duration
   */
  public void tween(float v, float d) {
    $tween(v, d).start(uiTweenMgr);
  }

  /**
   * @param v value
   */
  public void tween(float v) {
    $tween(v).start(uiTweenMgr);
  }

  public void tween() {
    $tween().start(uiTweenMgr);
  }

  /**
   * @param v value
   * @param d duration
   * @param eq equation
   */
  public Tween $tween(float v, float d, TweenEquation eq) {
    killTweenTarget(progress);
    return Tween.to(progress, 0, d).target(v).ease(eq);
  }

  /**
   * @param v value
   * @param d duration
   */
  public Tween $tween(float v, float d) {
    return $tween(v, d, Soft.INOUT);
  }

  /**
   * @param v value
   */
  public Tween $tween(float v) {
    return $tween(v, C_ID);
  }

  public Tween $tween() {
    return $tween(1);
  }

  /**
   * @param v value
   */
  public Tween $set(float v) {
    return $tween(v, 0);
  }

  public Tween $reset() {
    return $set(0);
  }

  @Override public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    float x = getX();
    float y = getY();
    float width = getWidth();
    float height = getHeight();
    float rotation = getRotation();

    // nested position
    float nx = x;
    float ny = y;
    Group parent = getParent();
    while (parent != getStage().getRoot()) {
      nx += parent.getX();
      ny += parent.getY();
      parent = parent.getParent();
    }

    batch.end();
    Gdx.gl.glClearDepthf(1.0f);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glColorMask(false, false, false, false);
    Gdx.gl.glDepthFunc(GL20.GL_LESS);
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glDepthMask(true);

    shr.begin(ShapeType.Filled);
      shr.setColor(1, 1, 1, .5f);
      if (clockwise) {
        shr.arc(nx + radius, ny + radius, radius + 10, rotation - degrees() + 90, degrees());
      } else {
        shr.arc(nx + radius, ny + radius, radius + 10, rotation + 90, degrees());
      }
    shr.end();

    Gdx.gl.glColorMask(true, true, true, true);
    Gdx.gl.glDepthMask(true);
    Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

    batch.begin();
      drawable.draw(batch, x, y, width, height);
    batch.end();

    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    batch.begin();
  }

  @Override public float getPrefWidth() {
    return radius * 2;
  }

  @Override public float getPrefHeight() {
    return radius * 2;
  }
}