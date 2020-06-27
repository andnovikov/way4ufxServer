package ru.diasoft.micro.way4ufx.model;

/**
 * UFXMsg.MsgData.Doc.TransType.TransCode.MsgCode
 * возможно нужно переделать в енум.
 *
 «01000B», это ответ на запрос баланса 
 «01000S», это ответ на запрос выписки 
 «02000F», это ответ на дебетование счета 
 «04200F», это ответ на отмену дебетования счета 
 «02200P», это ответ на кредитование счета
 «04200P», это ответ на отмену кредитования счета 
 «billing_payment_biller_check», это ответ на проверку платежа 
 «billing_payment_with_card», это подтверждение платежа 

 * @author V. Plasteev
 * @since 13.08.2014.
 * @version $Id$
 */
public class Constants {

    public static String MSG_01000B = "01000B";
    public static String MSG_01000S = "01000S";
    public static String MSG_02000F = "02000F";
    public static String MSG_02200P = "02200P";
    public static String MSG_04000F = "04000F";
    public static String MSG_04200F = "04200F";
    public static String MSG_04200P = "04200P";
    public static String MSG_BILLING_PAYMENT_BILLER_CHECK = "billing_payment_biller_check";
    public static String MSG_BILLING_PAYMENT_WITH_CARD = "billing_payment_with_card";

    public static String UCS_NO_CONTRACT_CHECK =   "UCS_NO_CONTRACT_CHECK";

}
