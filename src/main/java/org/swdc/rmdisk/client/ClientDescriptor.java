package org.swdc.rmdisk.client;

import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.rmdisk.client.descriptor.StarClientDescriptor;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;

@ImplementBy({
        StarClientDescriptor.class
})
public interface ClientDescriptor {

    String getClientName();

    ClientFileProtocol createClient(String host);


}
