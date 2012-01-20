package org.apache.smscserver.test.messageManager;

import junit.framework.Assert;

import org.apache.smscserver.message.impl.ShortMessageImpl;
import org.apache.smscserver.smsclet.ShortMessage;
import org.apache.smscserver.smsclet.ShortMessageStatus;

public class SubmitSMTest extends MessageManagerTemplate {

    public void testSubmitSMMessage() throws Exception {
        ShortMessage sm = this.createMessage("test1");

        this.messageManager.submitSM(sm);
    }

    public void testSubmitSMMessageWithReplace() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test3");
        this.messageManager.submitSM(sm1);

        ShortMessageImpl sm2 = this.createMessage("test3");
        sm2.setReplaceIfPresent(true);
        this.messageManager.submitSM(sm2);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertEquals(sm2.getId(), sm1.getReplacedBy());
        Assert.assertEquals(sm1.getId(), sm2.getReplaced());
    }

    public void testSubmitSMMessageWithReplaceMultiExisting() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test4");
        this.messageManager.submitSM(sm1);

        Thread.sleep(1000);

        ShortMessageImpl sm2 = this.createMessage("test4");
        this.messageManager.submitSM(sm2);

        ShortMessageImpl smr = this.createMessage("test4");
        smr.setReplaceIfPresent(true);
        this.messageManager.submitSM(smr);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());
        smr = (ShortMessageImpl) this.messageManager.selectShortMessage(smr.getId());

        Assert.assertNull(sm1.getReplacedBy());
        Assert.assertEquals(smr.getId(), sm2.getReplacedBy());
        Assert.assertEquals(sm2.getId(), smr.getReplaced());
    }

    public void testSubmitSMMessageWithReplaceNoOriginal() throws Exception {
        ShortMessageImpl sm1 = this.createMessage("test2");
        this.messageManager.submitSM(sm1);

        sm1.setStatus(ShortMessageStatus.DELIVERED);
        this.messageManager.submitSM(sm1);

        ShortMessageImpl sm2 = this.createMessage("test2");
        sm2.setReplaceIfPresent(true);
        this.messageManager.submitSM(sm2);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertNull(sm1.getReplacedBy());
        Assert.assertNull(sm2.getReplaced());
    }
}
