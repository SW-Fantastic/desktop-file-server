package org.swdc.rmdisk.client.descriptor;

import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.rmdisk.client.ClientDescriptor;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.client.protocol.StarWebDAVClientProtocol;

@MultipleImplement(ClientDescriptor.class)
public class StarClientDescriptor implements ClientDescriptor {


    @Override
    public String getClientName() {
        return "繁星云";
    }

    @Override
    public ClientFileProtocol createClient(String host) {
        return new StarWebDAVClientProtocol(host);
    }

    @Override
    public String toString() {
        return getClientName();
    }

}
