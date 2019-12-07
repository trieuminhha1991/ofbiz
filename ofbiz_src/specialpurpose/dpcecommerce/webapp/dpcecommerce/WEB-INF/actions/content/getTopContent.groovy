import com.olbius.baseecommerce.backend.ContentUtils;

def hotContents = ContentUtils.getHotContent(delegator);
context.hotContents = hotContents;
