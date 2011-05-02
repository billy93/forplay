/**
 * Copyright 2011 The ForPlay Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package forplay.core;

/**
 * A utility class that helps keep track of resource loading.
 */
public class AssetWatcher {

  /**
   * Listener interface for AssetWatcher.
   */
  public interface Listener {

    /**
     * Called when all assets are done loading (or had an error).
     */
    void done();

    /**
     * Called for each asset that failed to load.
     */
    void error(Throwable e);
  }

  private int total, loaded, errors;
  private boolean start;
  private final Listener listener;

  @SuppressWarnings("rawtypes")
  private ResourceCallback callback = new ResourceCallback() {
    @Override
    public void done(Object resource) {
      ++loaded;
      maybeDone();
    }

    @Override
    public void error(Throwable e) {
      listener.error(e);
      ++errors;
    }
  };

  /**
   * Creates a new watcher.
   */
  public AssetWatcher(Listener listener) {
    this.listener = listener;
  }

  /**
   * Adds an image resource to be watched.
   */
  @SuppressWarnings("unchecked")
  public void add(Image image) {
    assert !start;

    image.addCallback(callback);
    ++total;
  }

  /**
   * Done adding resources; {@link Listener#done()} will be called as soon as all assets are done
   * being loaded.
   */
  public void start() {
    start = true;
    maybeDone();
  }

  private void maybeDone() {
    if (start && (loaded + errors == total)) {
      listener.done();
    }
  }
}
