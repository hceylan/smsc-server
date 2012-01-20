package org.apache.smscserver.test.messageManager;

import junit.framework.Assert;

import org.apache.smscserver.message.impl.ShortMessageImpl;
import org.apache.smscserver.smsclet.ShortMessageStatus;
import org.apache.smscserver.smsclet.SmscOriginalNotFoundException;

public class CancelSMTest extends MessageManagerTemplate {

    public void testCancelSMMessageWithReplaceNoOriginal() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test2");
        this.messageManager.submitSM(sm1);

        sm1.setStatus(ShortMessageStatus.DELIVERED);
        this.messageManager.submitSM(sm1);

        ShortMessageImpl sm2 = this.createMessage("test2");
        sm2.setReplaceIfPresent(true);

        try {
            this.messageManager.cancelSM(sm2);

            Assert.fail("must throw!");
        } catch (SmscOriginalNotFoundException e) {
            // success
        }

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertNull(sm2);
    }

    public void testSubmitSMMessageWithReplace() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test3");
        this.messageManager.submitSM(sm1);

        ShortMessageImpl sm2 = this.createMessage("test3");
        sm2.setReplaceIfPresent(true);
        this.messageManager.cancelSM(sm2);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertEquals(ShortMessageStatus.CANCELED, sm1.getStatus());
        Assert.assertNull(sm2);
    }

    public void testSubmitSMMessageWithReplaceMultiExisting() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test4");
        this.messageManager.submitSM(sm1);

        Thread.sleep(1000);

        ShortMessageImpl sm2 = this.createMessage("test4");
        this.messageManager.submitSM(sm2);

        ShortMessageImpl smr = this.createMessage("test4");
        smr.setReplaceIfPresent(true);
        this.messageManager.cancelSM(smr);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());
        smr = (ShortMessageImpl) this.messageManager.selectShortMessage(smr.getId());

        Assert.assertEquals(ShortMessageStatus.PENDING, sm1.getStatus());
        Assert.assertEquals(ShortMessageStatus.CANCELED, sm2.getStatus());
        Assert.assertNull(smr);
    }
}
