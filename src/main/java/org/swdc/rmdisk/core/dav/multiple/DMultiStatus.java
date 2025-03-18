package org.swdc.rmdisk.core.dav.multiple;


import org.swdc.rmdisk.core.xmlns.Namespace;
import org.swdc.rmdisk.core.xmlns.XmlProperty;

import java.util.List;

@XmlProperty(value = "D:multistatus", namespace = {
        @Namespace(prefix = "D", uri = "DAV:")
})
public class DMultiStatus {

    @XmlProperty(value = "D:response",wrapCollection = false)
    private List<DResponse> responses;

    public DMultiStatus() {
    }

    public DMultiStatus(List<DResponse> responses) {
        this.responses = responses;
    }


    public List<DResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<DResponse> responses) {
        this.responses = responses;
    }


}
