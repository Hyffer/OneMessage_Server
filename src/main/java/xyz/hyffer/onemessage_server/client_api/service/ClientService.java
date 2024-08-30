package xyz.hyffer.onemessage_server.client_api.service;

import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientException;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBody;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;

public interface ClientService {

    ClientResponse.GetContacts getContacts(ClientRequestBody.GetContacts requestBody) throws ClientException;

    ClientResponse.GetMessages getMessages(ClientRequestBody.GetMessages requestBody) throws ClientException;

    ClientResponse.UpdateState updateStatus(ClientRequestBody.UpdateState requestBody) throws ClientException;

    ClientResponse.PostMessage postMessage(ClientRequestBody.PostMessage requestBody) throws ClientException;

}
