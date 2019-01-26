package fr.umlv.tatoo.runtime.tools;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;

/** Acts as a projection. Allow to get the buffer
 *  or a view of the buffer in the parser processor.
 *  
 * @author Remi Forax
 *
 * @param <B> type of the buffer.
 * @param <D> type of data.
 */
public abstract class DataViewer<B,D> {
  /** Defines the projection.
   * @param buffer the buffer
   * @return the data sent to the semantic part.
   */
  public abstract D view(B buffer);
  
  /** Returns the identity projection.
   *  The buffer will be sent to the parser processor.
   * 
   * @return a data viewer that performs an identity projection.
   * 
   * @param <B> type of the buffer.
   */
  @SuppressWarnings("unchecked")
  public static <B> DataViewer<B,B> getIdentityDataViewer() {
    return (DataViewer<B,B>)identityViewer;
  }
  
  /** Returns the {@link TokenBuffer#view()} projection.
  *  The result of a call to will {@link TokenBuffer#view()} on the buffer
  *  will be sent to the parser processor.
  * 
  * @return a data viewer that performs a {@link TokenBuffer#view()} projection.
  * 
  * @param <D> type of view's items.
  */
  @SuppressWarnings("unchecked")
  public static <D> DataViewer<TokenBuffer<D>,D> getTokenBufferViewer() {
    return (DataViewer<TokenBuffer<D>,D>)tokenBufferViewer;
  }
  
  private final static DataViewer<?,?> identityViewer=new DataViewer<Object,Object>() {
    @Override
    public Object view(Object buffer) {
      return buffer;
    }
  };
  
  private final static DataViewer<? extends TokenBuffer<?>,Object> tokenBufferViewer=new DataViewer<TokenBuffer<?>,Object>() {
    @Override
    public Object view(TokenBuffer<?> buffer) {
      return buffer.view();
    }
  };
}
