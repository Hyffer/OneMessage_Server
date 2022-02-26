package xyz.hyffer.onemessage_server.client_api.service;

import xyz.hyffer.onemessage_server.client_api.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.payload.SendBody;

public interface ClientRequestService {

    SendBody.ResponseBody getContacts(RequestBody.RequestBody_get_contacts requestBody) throws UnexpectedPayloadException;

    SendBody.ResponseBody getMessages(RequestBody.RequestBody_get_messages requestBody) throws UnexpectedPayloadException;

    SendBody.ResponseBody updateStatus(RequestBody.RequestBody_update_status requestBody) throws UnexpectedPayloadException;

    SendBody.ResponseBody postMessage(RequestBody.RequestBody_post_message requestBody) throws UnexpectedPayloadException;

}
