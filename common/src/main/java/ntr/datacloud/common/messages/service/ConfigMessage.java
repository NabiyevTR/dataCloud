package ntr.datacloud.common.messages.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ConfigMessage extends  ServiceMessage {
   private String regexLogin;
   private String regexPass;
   private int maxFileFrame;
}
