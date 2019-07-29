package podChat.requestobject;


/**
 * Get Static Image of a GeoLocation
 *
 *  {string}   type           Map style (default standard-night)
 *   {int}      zoom           Map zoom (default 15)
 *   {object}   center         Lat & Lng of Map center as a JSON
 *   {int}      width          width of image in pixels (default 800px)
 *   {int}      height         height of image in pixels (default 600px)
 */
public class RequestMapStaticImage  extends BaseRequestMapStImage {


    public RequestMapStaticImage( Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestMapStImage.Builder<Builder>{



        public RequestMapStaticImage build() {
            return new RequestMapStaticImage(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
    }
}
