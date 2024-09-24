package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.ToString;
import xyz.hyffer.onemessage_server.model.MessageSegment;

import java.util.List;

/**
 * See <a href="docs/API_CS.md">API_CS.md</a>
 */
public abstract class ClientRequestBody {

    public static class GetContactsDeserializer extends ClientRequestBodyDeserializer<ClientRequestBody.GetContacts> {}

    @Getter
    @ToString
    @JsonDeserialize(using = GetContactsDeserializer.class)
    public static abstract class GetContacts extends ClientRequestBody {
        Boolean all_attr    = false;
        Integer limit       = 20;

        @Override
        public boolean isValid() {
            return all_attr != null
                    && limit != null && limit >= 0;
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class GetContacts1 extends GetContacts {
        Integer _CID_l;     // nullable, results the same as `0`
        Integer _CID_r;     // nullable, results the same as `max(type(_CID))`
        Integer pre_cOrd;
        Integer pre_sOrd;

        @Override
        public boolean isValid() {
            return super.isValid()
                    && (_CID_l == null || _CID_l >= 0)
                    && (_CID_r == null || _CID_r > 0)
                    && (_CID_l == null || _CID_r == null || _CID_l < _CID_r)
                    && (pre_cOrd == null || pre_cOrd > 0)
                    && (pre_sOrd == null || pre_sOrd > 0)
                    && (pre_cOrd == null || pre_sOrd == null || limit == 0);
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class GetContacts2 extends GetContacts {
        Boolean pinned;
        Integer post_lMRank;    // nullable, results the same as `max(type(lastMsgRank)) + 1`

        @Override
        public boolean isValid() {
            return super.isValid()
                    && pinned != null
                    && (post_lMRank == null || post_lMRank > 0);
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class GetContacts3 extends GetContacts {
        String key;

        @Override
        public boolean isValid() {
            return super.isValid()
                    && key != null && !key.isEmpty();
        }
    }

    public static class GetMessagesDeserializer extends ClientRequestBodyDeserializer<ClientRequestBody.GetMessages> {}

    @Getter
    @ToString
    @JsonDeserialize(using = GetMessagesDeserializer.class)
    public static abstract class GetMessages extends ClientRequestBody {
        Integer limit       = 20;

        @Override
        public boolean isValid() {
            return limit != null && limit >= 0;
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class GetMessages1 extends GetMessages {
        Integer _MID_l;     // nullable, results the same as `0`
        Integer _MID_r;     // nullable, results the same as `max(type(_MID))`
        Integer pre_rank;
        Integer pre_cOrd;

        @Override
        public boolean isValid() {
            return super.isValid()
                    && (_MID_l == null || _MID_l >= 0)
                    && (_MID_r == null || _MID_r > 0)
                    && (_MID_l == null || _MID_r == null || _MID_l < _MID_r)
                    && (pre_rank == null || pre_rank > 0)
                    && (pre_cOrd == null || pre_cOrd > 0)
                    && (pre_rank == null || pre_cOrd == null || limit == 0);
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class GetMessages2 extends GetMessages {
        Integer _CID;
        Integer post_rank;  // nullable, results the same as `max(type(_MID)) + 1`

        @Override
        public boolean isValid() {
            return super.isValid()
                    && _CID != null && _CID > 0
                    && (post_rank == null || post_rank > 0);
        }
    }

    @Getter
    @ToString
    public static class UpdateState extends ClientRequestBody {
        Integer _CID;
        Boolean read;

        @Override
        public boolean isValid() {
            return _CID != null && _CID > 0;
        }
    }

    @Getter
    @ToString
    public static class PostMessage extends ClientRequestBody {
        Integer _CID;
        Integer _CiID   = 0;
        List<MessageSegment> content;

        @Override
        public boolean isValid() {
            return _CID != null && _CID > 0
                    && _CiID != null && _CiID >= 0
                    && content != null && !content.isEmpty();
        }
    }

    public abstract boolean isValid();

}
