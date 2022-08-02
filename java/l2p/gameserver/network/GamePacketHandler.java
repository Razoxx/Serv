package l2p.gameserver.network;

import l2p.gameserver.clientpackets.Say2C;
import l2p.gameserver.clientpackets.RequestChangeNicknameColor;
import l2p.gameserver.clientpackets.RequestCreatePledge;
import l2p.gameserver.clientpackets.RequestGiveItemToPet;
import l2p.gameserver.clientpackets.RequestExStopShowCrataeCubeRank;
import l2p.gameserver.clientpackets.RequestExFishRanking;
import l2p.gameserver.clientpackets.RequestRecipeShopMakeDo;
import l2p.gameserver.clientpackets.SetPrivateStoreMsgBuy;
import l2p.gameserver.clientpackets.RequestWithDrawalParty;
import l2p.gameserver.clientpackets.RequestModifyBookMarkSlot;
import l2p.gameserver.clientpackets.UseItem;
import l2p.gameserver.clientpackets.RequestAllyInfo;
import l2p.gameserver.clientpackets.RequestChangePetName;
import l2p.gameserver.clientpackets.RequestGmList;
import l2p.gameserver.clientpackets.RequestExBR_GamePoint;
import l2p.gameserver.clientpackets.RequestPledgeReorganizeMember;
import l2p.gameserver.clientpackets.RequestPVPMatchRecord;
import l2p.gameserver.clientpackets.RequestSaveKeyMapping;
import l2p.gameserver.clientpackets.RequestGoodsInventoryInfo;
import l2p.gameserver.clientpackets.NewCharacter;
import l2p.gameserver.clientpackets.RequestRecipeShopMakeInfo;
import l2p.gameserver.clientpackets.RequestExBR_RecentProductList;
import l2p.gameserver.clientpackets.RequestReload;
import l2p.gameserver.clientpackets.RequestExRemoveItemAttribute;
import l2p.gameserver.clientpackets.RequestPledgeCrestLarge;
import l2p.gameserver.clientpackets.AttackRequest;
import l2p.gameserver.clientpackets.RequestRecipeItemMakeInfo;
import l2p.gameserver.clientpackets.RequestPartyMatchDetail;
import l2p.gameserver.clientpackets.RequestAllCastleInfo;
import l2p.gameserver.clientpackets.RequestBookMarkSlotInfo;
import l2p.gameserver.clientpackets.RequestHennaList;
import l2p.gameserver.clientpackets.RequestSEKCustom;
import l2p.gameserver.clientpackets.RequestSetCastleSiegeTime;
import l2p.gameserver.clientpackets.Appearing;
import l2p.gameserver.clientpackets.RequestGMCommand;
import l2p.gameserver.clientpackets.RequestRefineCancel;
import l2p.gameserver.clientpackets.RequestShortCutReg;
import l2p.gameserver.clientpackets.EnterWorld;
import l2p.gameserver.clientpackets.RequestJoinPledge;
import l2p.gameserver.clientpackets.RequestBuySeed;
import l2p.gameserver.clientpackets.RequestPledgeExtendedInfo;
import l2p.gameserver.clientpackets.RequestExEnchantSkillRouteChange;
import l2p.gameserver.clientpackets.RequestEx2ndPasswordReq;
import l2p.gameserver.clientpackets.RequestDestroyItem;
import l2p.gameserver.clientpackets.RequestSendL2FriendSay;
import l2p.gameserver.clientpackets.RequestRestartPoint;
import l2p.gameserver.clientpackets.RequestDuelSurrender;
import l2p.gameserver.clientpackets.RequestCursedWeaponList;
import l2p.gameserver.clientpackets.RequestGiveNickName;
import l2p.gameserver.clientpackets.RequestPCCafeCouponUse;
import l2p.gameserver.clientpackets.CharacterRestore;
import l2p.gameserver.clientpackets.RequestCursedWeaponLocation;
import l2p.gameserver.clientpackets.RequestExDeleteReceivedPost;
import l2p.gameserver.clientpackets.RequestBuyItem;
import l2p.gameserver.clientpackets.RequestExMPCCAcceptJoin;
import l2p.gameserver.clientpackets.RequestFriendInvite;
import l2p.gameserver.clientpackets.RequestPackageSendableItemList;
import l2p.gameserver.clientpackets.RequestRecipeShopSellList;
import l2p.gameserver.clientpackets.RequestAnswerJoinAlly;
import l2p.gameserver.clientpackets.RequestFriendAddReply;
import l2p.gameserver.clientpackets.RequestExRequestSentPost;
import l2p.gameserver.clientpackets.RequestHennaItemInfo;
import l2p.gameserver.clientpackets.RequestTeleportBookMark;
import l2p.gameserver.clientpackets.RequestSaveInventoryOrder;
import l2p.gameserver.clientpackets.RequestPetition;
import l2p.gameserver.clientpackets.RequestSetPledgeCrestLarge;
import l2p.gameserver.clientpackets.FinishRotatingC;
import l2p.gameserver.clientpackets.RequestPartyLootModification;
import l2p.gameserver.clientpackets.RequestRecipeItemDelete;
import l2p.gameserver.clientpackets.RequestExEnchantSkillUntrain;
import l2p.gameserver.clientpackets.RequestTargetCanceld;
import l2p.gameserver.clientpackets.RequestHennaUnequip;
import l2p.gameserver.clientpackets.RequestAllFortressInfo;
import l2p.gameserver.clientpackets.RequestHennaUnequipInfo;
import l2p.gameserver.clientpackets.CharacterSelected;
import l2p.gameserver.clientpackets.RequestExStartShowCrataeCubeRank;
import l2p.gameserver.clientpackets.RequestStatus;
import l2p.gameserver.clientpackets.RequestExSeedPhase;
import l2p.gameserver.clientpackets.ProtocolVersion;
import l2p.gameserver.clientpackets.RequestExRejectPost;
import l2p.gameserver.clientpackets.RequestExCancelSentPost;
import l2p.gameserver.clientpackets.RequestItemList;
import l2p.gameserver.clientpackets.RequestSaveBookMarkSlot;
import l2p.gameserver.clientpackets.RequestMakeMacro;
import l2p.gameserver.clientpackets.NetPing;
import l2p.gameserver.clientpackets.RequestSiegeInfo;
import l2p.gameserver.clientpackets.RequestExEventMatchObserverEnd;
import l2p.gameserver.clientpackets.RequestConfirmCastleSiegeWaitingList;
import l2p.gameserver.clientpackets.RequestAllyCrest;
import l2p.gameserver.clientpackets.RequestExSendPost;
import l2p.gameserver.clientpackets.RequestExMpccPartymasterList;
import l2p.gameserver.clientpackets.RequestCrystallizeItem;
import l2p.gameserver.clientpackets.RequestExShowNewUserPetition;
import l2p.gameserver.clientpackets.RequestExBR_ProductList;
import l2p.gameserver.clientpackets.RequestAnswerJoinPledge;
import l2p.gameserver.clientpackets.StartRotatingC;
import l2p.gameserver.clientpackets.RequestBR_MiniGameInsertScore;
import l2p.gameserver.clientpackets.TradeRequest;
import l2p.gameserver.clientpackets.RequestDuelStart;
import l2p.gameserver.clientpackets.RequestExReceivePost;
import l2p.gameserver.clientpackets.RequestRecipeShopListSet;
import l2p.gameserver.clientpackets.RequestSetSeed;
import l2p.gameserver.clientpackets.RequestBR_MiniGameLoadScores;
import l2p.gameserver.clientpackets.RequestExMoveToLocationInAirShip;
import l2p.gameserver.clientpackets.SendWareHouseDepositList;
import l2p.gameserver.clientpackets.RequestQuestList;
import l2p.gameserver.clientpackets.RequestTutorialClientEvent;
import l2p.gameserver.clientpackets.RequestGetOffVehicle;
import l2p.gameserver.clientpackets.AnswerCoupleAction;
import l2p.gameserver.clientpackets.RequestBidItemAuction;
import l2p.gameserver.clientpackets.RequestPledgeInfo;
import l2p.gameserver.clientpackets.RequestGetOnVehicle;
import l2p.gameserver.clientpackets.RequestOustFromPartyRoom;
import l2p.gameserver.clientpackets.RequestPledgeMemberPowerInfo;
import l2p.gameserver.clientpackets.RequestExEnchantSkillInfo;
import l2p.gameserver.clientpackets.MoveBackwardToLocation;
import l2p.gameserver.clientpackets.AddTradeItem;
import l2p.gameserver.clientpackets.RequestEx2ndPasswordCheck;
import l2p.gameserver.clientpackets.MoveWithDelta;
import l2p.gameserver.clientpackets.RequestExMoveToLocationAirShip;
import l2p.gameserver.clientpackets.RequestConfirmTargetItem;
import l2p.gameserver.clientpackets.CannotMoveAnymoreInVehicle;
import l2p.gameserver.clientpackets.RequestProcureCropList;
import l2p.gameserver.clientpackets.CharacterDelete;
import l2p.gameserver.clientpackets.RequestExRefundItem;
import l2p.gameserver.clientpackets.RequestPetUseItem;
import l2p.gameserver.clientpackets.RequestMultiSellChoose;
import l2p.gameserver.clientpackets.SetPrivateStoreSellList;
import l2p.gameserver.clientpackets.RequestJoinParty;
import l2p.gameserver.clientpackets.RequestDismissAlly;
import l2p.gameserver.clientpackets.RequestStartPledgeWar;
import l2p.gameserver.clientpackets.RequestExMagicSkillUseGround;
import l2p.gameserver.clientpackets.RequestExBR_BuyProduct;
import l2p.gameserver.clientpackets.ValidatePosition;
import l2p.gameserver.clientpackets.RequestRecipeShopMessageSet;
import l2p.gameserver.clientpackets.RequestOustAlly;
import l2p.gameserver.clientpackets.RequestJoinAlly;
import l2p.gameserver.clientpackets.RequestDismissParty;
import l2p.gameserver.clientpackets.RequestManorList;
import l2p.gameserver.clientpackets.RequestActionUse;
import l2p.gameserver.clientpackets.RequestEx2ndPasswordVerify;
import l2p.gameserver.clientpackets.RequestExEnchantSkillInfoDetail;
import l2p.gameserver.clientpackets.RequestAquireSkill;
import l2p.gameserver.clientpackets.RequestExTryToPutEnchantSupportItem;
import l2p.gameserver.clientpackets.RequestExCleftEnter;
import l2p.gameserver.clientpackets.RequestPledgeMemberInfo;
import l2p.gameserver.clientpackets.RequestExEnchantSkill;
import l2p.gameserver.clientpackets.RequestPartyMatchList;
import l2p.gameserver.clientpackets.RequestExChangeName;
import l2p.gameserver.clientpackets.AuthLogin;
import l2p.gameserver.clientpackets.Logout;
import l2p.gameserver.clientpackets.RequestRecipeItemMakeSelf;
import l2p.gameserver.clientpackets.RequestFriendList;
import l2p.gameserver.clientpackets.SendBypassBuildCmd;
import l2p.gameserver.clientpackets.RequestShowBoard;
import l2p.gameserver.clientpackets.RequestExBuySellUIClose;
import l2p.gameserver.clientpackets.RequestPledgeSetAcademyMaster;
import l2p.gameserver.clientpackets.RequestPartyMatchConfig;
import l2p.gameserver.clientpackets.RequestTutorialLinkHtml;
import l2p.gameserver.clientpackets.RequestBypassToServer;
import l2p.gameserver.clientpackets.RequestTeleport;
import l2p.gameserver.clientpackets.SetPrivateStoreMsgSell;
import l2p.gameserver.clientpackets.RequestBBSwrite;
import l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade;
import l2p.gameserver.clientpackets.RequestExPostItemList;
import l2p.gameserver.clientpackets.RequestConfirmRefinerItem;
import l2p.gameserver.clientpackets.RequestAquireSkillInfo;
import l2p.gameserver.clientpackets.RequestExitPartyMatchingWaitingRoom;
import l2p.gameserver.clientpackets.RequestOlympiadMatchList;
import l2p.gameserver.clientpackets.RequestPledgeMemberList;
import l2p.gameserver.clientpackets.RequestPreviewItem;
import l2p.gameserver.clientpackets.RequestPrivateStoreList;
import l2p.gameserver.clientpackets.RequestStopPledgeWar;
import l2p.gameserver.clientpackets.RequestConfirmGemStone;
import l2p.gameserver.clientpackets.RequestExDeleteSentPost;
import l2p.gameserver.clientpackets.RequestExOlympiadObserverEnd;
import l2p.gameserver.clientpackets.RequestRecipeShopManageCancel;
import l2p.gameserver.clientpackets.RequestPrivateStoreQuitSell;
import l2p.gameserver.clientpackets.RequestPackageSend;
import l2p.gameserver.clientpackets.RequestExRequestSentPostList;
import l2p.gameserver.clientpackets.RequestExCancelEnchantItem;
import l2p.gameserver.clientpackets.RequestSetAllyCrest;
import l2p.gameserver.clientpackets.RequestDuelAnswerStart;
import l2p.gameserver.clientpackets.RequestSetPledgeCrest;
import l2p.gameserver.clientpackets.CannotMoveAnymore;
import l2p.gameserver.clientpackets.RequestPledgePowerGradeList;
import l2p.gameserver.clientpackets.RequestExShowStepTwo;
import l2p.gameserver.clientpackets.RequestAddExpandQuestAlarm;
import l2p.gameserver.clientpackets.RequestSkillList;
import l2p.gameserver.clientpackets.RequestListPartyMatchingWaitingRoom;
import l2p.gameserver.clientpackets.RequestPrivateStoreBuy;
import l2p.gameserver.clientpackets.RequestPledgePower;
import l2p.gameserver.clientpackets.RequestExDominionInfo;
import l2p.gameserver.clientpackets.NotifyStartMiniGame;
import l2p.gameserver.clientpackets.RequestHandOverPartyMaster;
import l2p.gameserver.clientpackets.RequestConfirmCancelItem;
import l2p.gameserver.clientpackets.RequestBlock;
import l2p.gameserver.clientpackets.PetitionVote;
import l2p.gameserver.clientpackets.RequestOustPartyMember;
import l2p.gameserver.clientpackets.RequestRecipeShopManageQuit;
import l2p.gameserver.clientpackets.RequestExAddPostFriendForPostBox;
import l2p.gameserver.clientpackets.RequestWriteHeroWords;
import l2p.gameserver.clientpackets.RequestExJoinMpccRoom;
import l2p.gameserver.clientpackets.RequestPrivateStoreQuitBuy;
import l2p.gameserver.clientpackets.RequestTutorialPassCmdToServer;
import l2p.gameserver.clientpackets.TradeDone;
import l2p.gameserver.clientpackets.RequestExManageMpccRoom;
import l2p.gameserver.clientpackets.RequestExDeletePostFriendForPostBox;
import l2p.gameserver.clientpackets.RequestSSQStatus;
import l2p.gameserver.clientpackets.RequestDispel;
import l2p.gameserver.clientpackets.RequestCastleSiegeDefenderList;
import l2p.gameserver.clientpackets.ConfirmDlg;
import l2p.gameserver.clientpackets.AnswerJoinPartyRoom;
import l2p.gameserver.clientpackets.RequestExFriendListForPostBox;
import l2p.gameserver.clientpackets.RequestExMPCCAskJoin;
import l2p.gameserver.clientpackets.EchoRequest;
import l2p.gameserver.clientpackets.RequestExWithdrawMpccRoom;
import l2p.gameserver.clientpackets.RequestWithdrawalPledge;
import l2p.gameserver.clientpackets.RequestJoinCastleSiege;
import l2p.gameserver.clientpackets.RequestExOustFromMpccRoom;
import l2p.gameserver.clientpackets.RequestExCubeGameChangeTeam;
import l2p.gameserver.clientpackets.RequestWithdrawAlly;
import l2p.gameserver.clientpackets.RequestMagicSkillUse;
import l2p.gameserver.clientpackets.RequestExJump;
import l2p.gameserver.clientpackets.AnswerTradeRequest;
import l2p.gameserver.clientpackets.RequestRecipeBookOpen;
import l2p.gameserver.clientpackets.RequestFriendDel;
import l2p.gameserver.clientpackets.RequestExRequestReceivedPost;
import l2p.gameserver.clientpackets.Action;
import l2p.gameserver.clientpackets.RequestExMPCCShowPartyMembersInfo;
import l2p.gameserver.clientpackets.RequestExRqItemLink;
import l2p.gameserver.clientpackets.RequestAutoSoulShot;
import l2p.gameserver.clientpackets.RequestExEndScenePlayer;
import l2p.gameserver.clientpackets.RequestObserverEnd;
import l2p.gameserver.clientpackets.SnoopQuit;
import l2p.gameserver.clientpackets.RequestKeyMapping;
import l2p.gameserver.clientpackets.SetPrivateStoreBuyList;
import l2p.gameserver.clientpackets.RequestGetItemFromPet;
import l2p.gameserver.clientpackets.RequestMoveToLocationInVehicle;
import l2p.gameserver.clientpackets.RequestWithDrawPremiumItem;
import l2p.gameserver.clientpackets.RequestWithdrawPartyRoom;
import l2p.gameserver.clientpackets.RequestResetNickname;
import l2p.gameserver.clientpackets.RequestExListMpccWaiting;
import l2p.gameserver.clientpackets.RequestExBR_LectureMark;
import l2p.gameserver.clientpackets.RequestRefine;
import l2p.gameserver.clientpackets.RequestQuestAbort;
import l2p.gameserver.clientpackets.RequestExBR_ProductInfo;
import l2p.gameserver.clientpackets.RequestPrivateStoreBuyManage;
import l2p.gameserver.clientpackets.CharacterCreate;
import l2p.gameserver.clientpackets.RequestExOustFromMPCC;
import l2p.gameserver.clientpackets.SendWareHouseWithDrawList;
import l2p.gameserver.clientpackets.RequestPetitionCancel;
import l2p.gameserver.clientpackets.RequestGetBossRecord;
import l2p.gameserver.clientpackets.RequestSetCrop;
import l2p.gameserver.clientpackets.BypassUserCmd;
import l2p.gameserver.clientpackets.RequestExTryToPutEnchantTargetItem;
import l2p.gameserver.clientpackets.RequestExDismissMpccRoom;
import l2p.gameserver.clientpackets.GotoLobby;
import l2p.gameserver.clientpackets.RequestExEnchantSkillSafe;
import l2p.gameserver.clientpackets.RequestInfoItemAuction;
import l2p.gameserver.clientpackets.RequestExBR_EventRankerList;
import l2p.gameserver.clientpackets.RequestDeleteBookMarkSlot;
import l2p.gameserver.clientpackets.RequestVoteNew;
import l2p.gameserver.clientpackets.RequestProcureCrop;
import l2p.gameserver.clientpackets.RequestExJoinDominionWar;
import l2p.gameserver.clientpackets.RequestHennaUnequipList;
import l2p.gameserver.clientpackets.RequestAnswerJoinParty;
import l2p.gameserver.clientpackets.RequestShowMiniMap;
import l2p.gameserver.clientpackets.RequestSellItem;
import l2p.gameserver.clientpackets.RequestPledgeCrest;
import l2p.gameserver.clientpackets.RequestRestart;
import l2p.gameserver.clientpackets.RequestFortressSiegeInfo;
import l2p.gameserver.clientpackets.SetPrivateStoreWholeMsg;
import l2p.gameserver.clientpackets.RequestPrivateStoreBuySellList;
import l2p.gameserver.clientpackets.RequestExShowStepThree;
import l2p.gameserver.clientpackets.RequestExCubeGameReadyAnswer;
import l2p.gameserver.clientpackets.RequestExShowPostFriendListForPostBox;
import l2p.gameserver.clientpackets.RequestHennaEquip;
import l2p.gameserver.clientpackets.RequestFortressMapInfo;
import l2p.gameserver.clientpackets.RequestSendMsnChatLog;
import l2p.gameserver.clientpackets.RequestMagicSkillList;
import l2p.gameserver.clientpackets.RequestShortCutDel;
import l2p.gameserver.clientpackets.RequestEnchantItemAttribute;
import l2p.gameserver.clientpackets.RequestDismissPartyRoom;
import l2p.gameserver.clientpackets.RequestCastleSiegeAttackerList;
import l2p.gameserver.clientpackets.RequestRemainTime;
import l2p.gameserver.clientpackets.AnswerPartyLootModification;
import l2p.gameserver.clientpackets.RequestAllAgitInfo;
import l2p.gameserver.clientpackets.RequestDeleteMacro;
import l2p.gameserver.clientpackets.RequestDropItem;
import l2p.gameserver.clientpackets.RequestPetGetItem;
import l2p.gameserver.clientpackets.RequestAskJoinPartyRoom;
import l2p.gameserver.clientpackets.RequestEnchantItem;
import l2p.gameserver.clientpackets.RequestTimeCheck;
import l2p.gameserver.clientpackets.RequestExRequestReceivedPostList;
import l2p.gameserver.clientpackets.RequestPledgeWarList;
import l2p.gameserver.clientpackets.RequestTutorialQuestionMark;
import l2p.gameserver.clientpackets.RequestOustPledgeMember;
import l2p.gameserver.clientpackets.RequestOlympiadObserverEnd;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import l2p.commons.net.nio.impl.IClientFactory;
import l2p.commons.net.nio.impl.IMMOExecutor;
import l2p.commons.net.nio.impl.IPacketHandler;
import l2p.commons.net.nio.impl.MMOConnection;
import l2p.commons.net.nio.impl.ReceivablePacket;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.clientpackets.ReplyGameGuardQuery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GamePacketHandler implements IPacketHandler<GameClient>, IClientFactory<GameClient>, IMMOExecutor<GameClient> {

    private static final Logger _log = LoggerFactory.getLogger(GamePacketHandler.class);

    @SuppressWarnings("static-access")
    @Override
    public ReceivablePacket<GameClient> handlePacket(ByteBuffer buf, GameClient client) {
        int id = buf.get() & 0xFF;

        ReceivablePacket<GameClient> msg = null;

        try {
            int id2 = 0;
            switch (client.getState()) {
                case CONNECTED:
                    switch (id) {
                        case 0x00:
                            msg = new RequestStatus();
                            break;
                        case 0x0e:
                            msg = new ProtocolVersion();
                            break;
                        case 0x2b:
                            msg = new AuthLogin();
                            break;
                        case 0xCB:
                            msg = new ReplyGameGuardQuery();
                            break;
                        case 0x9F:
                            msg = new EchoRequest();
                            break;
                        default:
                            client.onUnknownPacket();
                            break;
                    }
                    break;
                case AUTHED:
                    switch (id) {
                        case 0x00:
                            msg = new Logout();
                            break;
                        case 0x0c:
                            msg = new CharacterCreate(); //RequestCharacterCreate();
                            break;
                        case 0x0d:
                            msg = new CharacterDelete(); //RequestCharacterDelete();
                            break;
                        case 0x12:
                            msg = new CharacterSelected(); //CharacterSelect();
                            break;
                        case 0x13:
                            msg = new NewCharacter(); //RequestNewCharacter();
                            break;
                        case 0x7b:
                            msg = new CharacterRestore(); //RequestCharacterRestore();
                            break;
                        case 0xCB:
                            msg = new ReplyGameGuardQuery();
                            break;
                        case 0xd0:
                            int id3 = buf.getShort() & 0xffff;
                            switch (id3) {
                                case 0x36:
                                    msg = new GotoLobby();
                                    break;
                                case 0x93:
                                    msg = new RequestEx2ndPasswordCheck();
                                    break;
                                case 0x94:
                                    msg = new RequestEx2ndPasswordVerify();
                                    break;
                                case 0x95:
                                    msg = new RequestEx2ndPasswordReq();
                                    break;
                                default:
                                    client.onUnknownPacket();
                                    break;
                            }
                            break;
                        default:
                            client.onUnknownPacket();
                            break;
                    }
                    break;
                case IN_GAME:
                    switch (id) {
                        case 0x00:
                            msg = new Logout();
                            break;
                        case 0x01:
                            msg = new AttackRequest();
                            break;
                        case 0x02:
                            //	msg = new ?();
                            break;
                        case 0x03:
                            msg = new RequestStartPledgeWar();
                            break;
                        case 0x04:
                            //	msg = new ?();
                            break;
                        case 0x05:
                            msg = new RequestStopPledgeWar();
                            break;
                        case 0x06:
                            //	msg = RequestReplyStopPledgeWar(); // ? Format: cdx
                            break;
                        case 0x07:
                            //	msg = new ReplyGameGuardQuery();
                            //здесь совсем другой пакет ResponseAuthGameGuard[cddddd] (c) Drin
                            break;
                        case 0x08:
                            //	msg = new ?();
                            break;
                        case 0x09:
                            msg = new RequestSetPledgeCrest();
                            break;
                        case 0x0a:
                            //	msg = new ?();
                            break;
                        case 0x0b:
                            msg = new RequestGiveNickName();
                            break;
                        case 0x0c:
                            //	wtf???
                            break;
                        case 0x0d:
                            //	wtf???
                            break;
                        case 0x0f:
                            msg = new MoveBackwardToLocation();
                            break;
                        case 0x10:
                            //	msg = new Say(); Format: cS // старый ?
                            break;
                        case 0x11:
                            msg = new EnterWorld();
                            break;
                        case 0x12:
                            //	wtf???
                            break;
                        case 0x14:
                            msg = new RequestItemList();
                            break;
                        case 0x15:
                            //	msg = new RequestEquipItem(); // старый?
                            //	Format: cdd server id = %d Slot = %d
                            break;
                        case 0x16:
                            //msg = new RequestUnEquipItem();
                            break;
                        case 0x17:
                            msg = new RequestDropItem();
                            break;
                        case 0x18:
                            //	msg = new ?();
                            break;
                        case 0x19:
                            msg = new UseItem();
                            break;
                        case 0x1a:
                            msg = new TradeRequest();
                            break;
                        case 0x1b:
                            msg = new AddTradeItem();
                            break;
                        case 0x1c:
                            msg = new TradeDone();
                            break;
                        case 0x1d:
                            //	msg = new ?();
                            break;
                        case 0x1e:
                            //	msg = new ?();
                            break;
                        case 0x1f:
                            msg = new Action();
                            break;
                        case 0x20:
                            //	msg = new ?();
                            break;
                        case 0x21:
                            //	msg = new ?();
                            break;
                        case 0x22:
                            // msg = new RequestLinkHtml();
                            break;
                        case 0x23:
                            msg = new RequestBypassToServer();
                            break;
                        case 0x24:
                            msg = new RequestBBSwrite(); //RequestBBSWrite();
                            break;
                        case 0x25:
                            msg = new RequestCreatePledge();
                            break;
                        case 0x26:
                            msg = new RequestJoinPledge();
                            break;
                        case 0x27:
                            msg = new RequestAnswerJoinPledge();
                            break;
                        case 0x28:
                            msg = new RequestWithdrawalPledge();
                            break;
                        case 0x29:
                            msg = new RequestOustPledgeMember();
                            break;
                        case 0x2a:
                            //	msg = new ?();
                            break;
                        case 0x2c:
                            msg = new RequestGetItemFromPet();
                            break;
                        case 0x2d:
                            //	RequestDismissParty
                            break;
                        case 0x2e:
                            msg = new RequestAllyInfo();
                            break;
                        case 0x2f:
                            msg = new RequestCrystallizeItem();
                            break;
                        case 0x30:
                            // RequestPrivateStoreManage, устарел
                            break;
                        case 0x31:
                            msg = new SetPrivateStoreSellList();
                            break;
                        case 0x32:
                            // RequestPrivateStoreManageCancel, устарел
                            break;
                        case 0x33:
                            msg = new RequestTeleport();
                            break;
                        case 0x34:
                            //msg = new RequestSocialAction();
                            break;
                        case 0x35:
                            // ChangeMoveType, устарел
                            break;
                        case 0x36:
                            // ChangeWaitType, устарел
                            break;
                        case 0x37:
                            msg = new RequestSellItem();
                            break;
                        case 0x38:
                            msg = new RequestMagicSkillList();
                            break;
                        case 0x39:
                            msg = new RequestMagicSkillUse();
                            break;
                        case 0x3a:
                            msg = new Appearing(); //Appering();
                            break;
                        case 0x3b:
                            if (Config.ALLOW_WAREHOUSE) {
                                msg = new SendWareHouseDepositList();
                            }
                            break;
                        case 0x3c:
                            msg = new SendWareHouseWithDrawList();
                            break;
                        case 0x3d:
                            msg = new RequestShortCutReg();
                            break;
                        case 0x3e:
                            //	msg = new RequestShortCutUse(); // Format: cddc  ?
                            break;
                        case 0x3f:
                            msg = new RequestShortCutDel();
                            break;
                        case 0x40:
                            msg = new RequestBuyItem();
                            break;
                        case 0x41:
                            //	msg = new RequestDismissPledge(); //Format: c ?
                            break;
                        case 0x42:
                            msg = new RequestJoinParty();
                            break;
                        case 0x43:
                            msg = new RequestAnswerJoinParty();
                            break;
                        case 0x44:
                            msg = new RequestWithDrawalParty();
                            break;
                        case 0x45:
                            msg = new RequestOustPartyMember();
                            break;
                        case 0x46:
                            msg = new RequestDismissParty();
                            break;
                        case 0x47:
                            msg = new CannotMoveAnymore();
                            break;
                        case 0x48:
                            msg = new RequestTargetCanceld();
                            break;
                        case 0x49:
                            msg = new Say2C();
                            break;
                        // -- maybe GM packet's
                        case 0x4a:
                            id2 = buf.get() & 0xff;
                            switch (id2) {
                                case 0x00:
                                    //	msg = new SendCharacterInfo(); // Format: S
                                    break;
                                case 0x01:
                                    //	msg = new SendSummonCmd(); // Format: S
                                    break;
                                case 0x02:
                                    //	msg = new SendServerStatus(); // Format: (noargs)
                                    break;
                                case 0x03:
                                    //	msg = new SendL2ParamSetting(); // Format: dd
                                    break;
                                default:
                                    client.onUnknownPacket();
                                    break;
                            }
                            break;
                        case 0x4b:
                            //	msg = new ?();
                            break;
                        case 0x4c:
                            //	msg = new ?();
                            break;
                        case 0x4d:
                            msg = new RequestPledgeMemberList();
                            break;
                        case 0x4e:
                            //	msg = new ?();
                            break;
                        case 0x4f:
                            //	msg = new RequestMagicItem(); // Format: c ?
                            break;
                        case 0x50:
                            msg = new RequestSkillList(); // trigger
                            break;
                        case 0x51:
                            //	msg = new ?();
                            break;
                        case 0x52:
                            msg = new MoveWithDelta();
                            break;
                        case 0x53:
                            msg = new RequestGetOnVehicle();
                            break;
                        case 0x54:
                            msg = new RequestGetOffVehicle();
                            break;
                        case 0x55:
                            msg = new AnswerTradeRequest();
                            break;
                        case 0x56:
                            msg = new RequestActionUse();
                            break;
                        case 0x57:
                            msg = new RequestRestart();
                            break;
                        case 0x58:
                            msg = new RequestSiegeInfo();
                            break;
                        case 0x59:
                            msg = new ValidatePosition();
                            break;
                        case 0x5a:
                            msg = new RequestSEKCustom();
                            break;
                        case 0x5b:
                            msg = new StartRotatingC();
                            break;
                        case 0x5c:
                            msg = new FinishRotatingC();
                            break;
                        case 0x5d:
                            //	msg = new ?();
                            break;
                        case 0x5e:
                            msg = new RequestShowBoard();
                            break;
                        case 0x5f:
                            msg = new RequestEnchantItem();
                            break;
                        case 0x60:
                            msg = new RequestDestroyItem();
                            break;
                        case 0x61:
                            //	msg = new ?();
                            break;
                        case 0x62:
                            msg = new RequestQuestList();
                            break;
                        case 0x63:
                            msg = new RequestQuestAbort(); //RequestDestroyQuest();
                            break;
                        case 0x64:
                            //	msg = new ?();
                            break;
                        case 0x65:
                            msg = new RequestPledgeInfo();
                            break;
                        case 0x66:
                            msg = new RequestPledgeExtendedInfo();
                            break;
                        case 0x67:
                            msg = new RequestPledgeCrest();
                            break;
                        case 0x68:
                            //	msg = new ?();
                            break;
                        case 0x69:
                            //	msg = new ?();
                            break;
                        case 0x6a:
                            //	msg = new ?();
                            break;
                        case 0x6b:
                            msg = new RequestSendL2FriendSay();
                            break;
                        case 0x6c:
                            msg = new RequestShowMiniMap(); //RequestOpenMinimap();
                            break;
                        case 0x6d:
                            msg = new RequestSendMsnChatLog();
                            break;
                        case 0x6e:
                            msg = new RequestReload(); // record video
                            break;
                        case 0x6f:
                            msg = new RequestHennaEquip();
                            break;
                        case 0x70:
                            msg = new RequestHennaUnequipList();
                            break;
                        case 0x71:
                            msg = new RequestHennaUnequipInfo();
                            break;
                        case 0x72:
                            msg = new RequestHennaUnequip();
                            break;
                        case 0x73:
                            msg = new RequestAquireSkillInfo(); //RequestAcquireSkillInfo();
                            break;
                        case 0x74:
                            msg = new SendBypassBuildCmd();
                            break;
                        case 0x75:
                            msg = new RequestMoveToLocationInVehicle();
                            break;
                        case 0x76:
                            msg = new CannotMoveAnymoreInVehicle();
                            break;
                        case 0x77:
                            msg = new RequestFriendInvite();
                            break;
                        case 0x78:
                            msg = new RequestFriendAddReply();
                            break;
                        case 0x79:
                            msg = new RequestFriendList();
                            break;
                        case 0x7a:
                            msg = new RequestFriendDel();
                            break;
                        case 0x7c:
                            msg = new RequestAquireSkill();
                            break;
                        case 0x7d:
                            msg = new RequestRestartPoint();
                            break;
                        case 0x7e:
                            msg = new RequestGMCommand();
                            break;
                        case 0x7f:
                            msg = new RequestPartyMatchConfig();
                            break;
                        case 0x80:
                            msg = new RequestPartyMatchList();
                            break;
                        case 0x81:
                            msg = new RequestPartyMatchDetail();
                            break;
                        case 0x82:
                            msg = new RequestPrivateStoreList();
                            break;
                        case 0x83:
                            msg = new RequestPrivateStoreBuy();
                            break;
                        case 0x84:
                            //	msg = new ReviveReply(); // format: cd ?
                            break;
                        case 0x85:
                            msg = new RequestTutorialLinkHtml();
                            break;
                        case 0x86:
                            msg = new RequestTutorialPassCmdToServer();
                            break;
                        case 0x87:
                            msg = new RequestTutorialQuestionMark(); //RequestTutorialQuestionMarkPressed();
                            break;
                        case 0x88:
                            msg = new RequestTutorialClientEvent();
                            break;
                        case 0x89:
                            msg = new RequestPetition();
                            break;
                        case 0x8a:
                            msg = new RequestPetitionCancel();
                            break;
                        case 0x8b:
                            msg = new RequestGmList();
                            break;
                        case 0x8c:
                            msg = new RequestJoinAlly();
                            break;
                        case 0x8d:
                            msg = new RequestAnswerJoinAlly();
                            break;
                        case 0x8e:
                            // Команда /allyleave - выйти из альянса
                            msg = new RequestWithdrawAlly();
                            break;
                        case 0x8f:
                            // Команда /allydismiss - выгнать клан из альянса
                            msg = new RequestOustAlly();
                            break;
                        case 0x90:
                            // Команда /allydissolve - распустить альянс
                            msg = new RequestDismissAlly();
                            break;
                        case 0x91:
                            msg = new RequestSetAllyCrest();
                            break;
                        case 0x92:
                            msg = new RequestAllyCrest();
                            break;
                        case 0x93:
                            msg = new RequestChangePetName();
                            break;
                        case 0x94:
                            msg = new RequestPetUseItem();
                            break;
                        case 0x95:
                            msg = new RequestGiveItemToPet();
                            break;
                        case 0x96:
                            msg = new RequestPrivateStoreQuitSell();
                            break;
                        case 0x97:
                            msg = new SetPrivateStoreMsgSell();
                            break;
                        case 0x98:
                            msg = new RequestPetGetItem();
                            break;
                        case 0x99:
                            msg = new RequestPrivateStoreBuyManage();
                            break;
                        case 0x9a:
                            msg = new SetPrivateStoreBuyList();
                            break;
                        case 0x9b:
                            //
                            break;
                        case 0x9c:
                            msg = new RequestPrivateStoreQuitBuy();
                            break;
                        case 0x9d:
                            msg = new SetPrivateStoreMsgBuy();
                            break;
                        case 0x9e:
                            //
                            break;
                        case 0x9f:
                            msg = new RequestPrivateStoreBuySellList();
                            break;
                        case 0xa0:
                            msg = new RequestTimeCheck();
                            break;
                        case 0xa1:
                            //	msg = new ?();
                            break;
                        case 0xa2:
                            //	msg = new ?();
                            break;
                        case 0xa3:
                            //	msg = new ?();
                            break;
                        case 0xa4:
                            //	msg = new ?();
                            break;
                        case 0xa5:
                            //	msg = new ?();
                            break;
                        case 0xa6:
                            //msg = new RequestSkillCoolTime();  //Deprecated ?
                            break;
                        case 0xa7:
                            msg = new RequestPackageSendableItemList();
                            break;
                        case 0xa8:
                            msg = new RequestPackageSend();
                            break;
                        case 0xa9:
                            msg = new RequestBlock();
                            break;
                        case 0xaa:
                            //	msg = new RequestCastleSiegeInfo(); // format: cd ?
                            break;
                        case 0xab:
                            msg = new RequestCastleSiegeAttackerList();
                            break;
                        case 0xac:
                            msg = new RequestCastleSiegeDefenderList();
                            break;
                        case 0xad:
                            msg = new RequestJoinCastleSiege();
                            break;
                        case 0xae:
                            msg = new RequestConfirmCastleSiegeWaitingList();
                            break;
                        case 0xaf:
                            msg = new RequestSetCastleSiegeTime();
                            break;
                        case 0xb0:
                            msg = new RequestMultiSellChoose();
                            break;
                        case 0xb1:
                            msg = new NetPing();
                            break;
                        case 0xb2:
                            msg = new RequestRemainTime();
                            break;
                        case 0xb3:
                            msg = new BypassUserCmd();
                            break;
                        case 0xb4:
                            msg = new SnoopQuit();
                            break;
                        case 0xb5:
                            msg = new RequestRecipeBookOpen();
                            break;
                        case 0xb6:
                            msg = new RequestRecipeItemDelete();
                            break;
                        case 0xb7:
                            msg = new RequestRecipeItemMakeInfo();
                            break;
                        case 0xb8:
                            msg = new RequestRecipeItemMakeSelf();
                            break;
                        case 0xb9:
                            // msg = new RequestRecipeShopManageList(); deprecated // format: c
                            break;
                        case 0xba:
                            msg = new RequestRecipeShopMessageSet();
                            break;
                        case 0xbb:
                            msg = new RequestRecipeShopListSet();
                            break;
                        case 0xbc:
                            msg = new RequestRecipeShopManageQuit();
                            break;
                        case 0xbd:
                            msg = new RequestRecipeShopManageCancel();
                            break;
                        case 0xbe:
                            msg = new RequestRecipeShopMakeInfo();
                            break;
                        case 0xbf:
                            msg = new RequestRecipeShopMakeDo();
                            break;
                        case 0xc0:
                            msg = new RequestRecipeShopSellList();
                            break;
                        case 0xc1:
                            msg = new RequestObserverEnd();
                            break;
                        case 0xc2:
                            //msg = new VoteSociality(); // Recommend
                            break;
                        case 0xc3:
                            msg = new RequestHennaList(); //RequestHennaItemList();
                            break;
                        case 0xc4:
                            msg = new RequestHennaItemInfo();
                            break;
                        case 0xc5:
                            msg = new RequestBuySeed();
                            break;
                        case 0xc6:
                            msg = new ConfirmDlg();
                            break;
                        case 0xc7:
                            msg = new RequestPreviewItem();
                            break;
                        case 0xc8:
                            msg = new RequestSSQStatus();
                            break;
                        case 0xc9:
                            msg = new PetitionVote();
                            break;
                        case 0xca:
                            //	msg = new ?();
                            break;
                        case 0xcb:
                            //msg = new GameGuardReply();
                            break;
                        case 0xcc:
                            msg = new RequestPledgePower();
                            break;
                        case 0xcd:
                            msg = new RequestMakeMacro();
                            break;
                        case 0xce:
                            msg = new RequestDeleteMacro();
                            break;
                        case 0xcf:
                            msg = new RequestProcureCrop(); // ?
                            break;
                        case 0xd0:
                            int id3 = buf.getShort() & 0xffff;
                            switch (id3) {
                                case 0x00:
                                    //	msg = new ?();
                                    break;
                                case 0x01:
                                    msg = new RequestManorList();
                                    break;
                                case 0x02:
                                    msg = new RequestProcureCropList();
                                    break;
                                case 0x03:
                                    msg = new RequestSetSeed();
                                    break;
                                case 0x04:
                                    msg = new RequestSetCrop();
                                    break;
                                case 0x05:
                                    msg = new RequestWriteHeroWords();
                                    break;
                                case 0x06:
                                    msg = new RequestExMPCCAskJoin(); //RequestExAskJoinMPCC();
                                    break;
                                case 0x07:
                                    msg = new RequestExMPCCAcceptJoin(); //RequestExAcceptJoinMPCC();
                                    break;
                                case 0x08:
                                    msg = new RequestExOustFromMPCC();
                                    break;
                                case 0x09:
                                    msg = new RequestOustFromPartyRoom();
                                    break;
                                case 0x0a:
                                    msg = new RequestDismissPartyRoom();
                                    break;
                                case 0x0b:
                                    msg = new RequestWithdrawPartyRoom();
                                    break;
                                case 0x0c:
                                    msg = new RequestHandOverPartyMaster();
                                    break;
                                case 0x0d:
                                    msg = new RequestAutoSoulShot();
                                    break;
                                case 0x0e:
                                    msg = new RequestExEnchantSkillInfo();
                                    break;
                                case 0x0f:
                                    msg = new RequestExEnchantSkill();
                                    break;
                                case 0x10:
                                    msg = new RequestPledgeCrestLarge();
                                    break;
                                case 0x11:
                                    msg = new RequestSetPledgeCrestLarge();
                                    break;
                                case 0x12:
                                    msg = new RequestPledgeSetAcademyMaster();
                                    break;
                                case 0x13:
                                    msg = new RequestPledgePowerGradeList();
                                    break;
                                case 0x14:
                                    msg = new RequestPledgeMemberPowerInfo();
                                    break;
                                case 0x15:
                                    msg = new RequestPledgeSetMemberPowerGrade();
                                    break;
                                case 0x16:
                                    msg = new RequestPledgeMemberInfo();
                                    break;
                                case 0x17:
                                    msg = new RequestPledgeWarList();
                                    break;
                                case 0x18:
                                    msg = new RequestExFishRanking();
                                    break;
                                case 0x19:
                                    msg = new RequestPCCafeCouponUse();
                                    break;
                                case 0x1a:
                                    //	msg = new ?();
                                    // format: (ch)b, b - array размером в 64 байта
                                    break;
                                case 0x1b:
                                    msg = new RequestDuelStart();
                                    break;
                                case 0x1c:
                                    msg = new RequestDuelAnswerStart();
                                    break;
                                case 0x1d:
                                    msg = new RequestTutorialClientEvent(); //RequestExSetTutorial();
                                    // Format: d / требует отладки, ИМХО, это совсем другой пакет (с) Drin
                                    break;
                                case 0x1e:
                                    msg = new RequestExRqItemLink(); // chat item links
                                    break;
                                case 0x1f:
                                    // CanNotMoveAnymore(AirShip)
                                    // format: (ch)ddddd
                                    break;
                                case 0x20:
                                    msg = new RequestExMoveToLocationInAirShip();
                                    break;
                                case 0x21:
                                    msg = new RequestKeyMapping();
                                    break;
                                case 0x22:
                                    msg = new RequestSaveKeyMapping();
                                    break;
                                case 0x23:
                                    msg = new RequestExRemoveItemAttribute();
                                    break;
                                case 0x24:
                                    msg = new RequestSaveInventoryOrder(); // сохранение порядка инвентаря
                                    break;
                                case 0x25:
                                    msg = new RequestExitPartyMatchingWaitingRoom();
                                    break;
                                case 0x26:
                                    msg = new RequestConfirmTargetItem();
                                    break;
                                case 0x27:
                                    msg = new RequestConfirmRefinerItem();
                                    break;
                                case 0x28:
                                    msg = new RequestConfirmGemStone();
                                    break;
                                case 0x29:
                                    msg = new RequestOlympiadObserverEnd();
                                    break;
                                case 0x2a:
                                    msg = new RequestCursedWeaponList();
                                    break;
                                case 0x2b:
                                    msg = new RequestCursedWeaponLocation();
                                    break;
                                case 0x2c:
                                    msg = new RequestPledgeReorganizeMember();
                                    break;
                                case 0x2d:
                                    msg = new RequestExMPCCShowPartyMembersInfo();
                                    break;
                                case 0x2e:
                                    msg = new RequestExOlympiadObserverEnd(); // не уверен (в клиенте называется RequestOlympiadMatchList)
                                    break;
                                case 0x2f:
                                    msg = new RequestAskJoinPartyRoom();
                                    break;
                                case 0x30:
                                    msg = new AnswerJoinPartyRoom();
                                    break;
                                case 0x31:
                                    msg = new RequestListPartyMatchingWaitingRoom();
                                    break;
                                case 0x32:
                                    msg = new RequestExEnchantSkillSafe();
                                    break;
                                case 0x33:
                                    msg = new RequestExEnchantSkillUntrain();
                                    break;
                                case 0x34:
                                    msg = new RequestExEnchantSkillRouteChange();
                                    break;
                                case 0x35:
                                    msg = new RequestEnchantItemAttribute();
                                    break;
                                case 0x36:
                                    //RequestGotoLobby - случается при многократном нажатии кнопки "вход"
                                    break;
                                case 0x38:
                                    msg = new RequestExMoveToLocationAirShip();
                                    break;
                                case 0x39:
                                    msg = new RequestBidItemAuction();
                                    break;
                                case 0x3a:
                                    msg = new RequestInfoItemAuction();
                                    break;
                                case 0x3b:
                                    msg = new RequestExChangeName();
                                    break;
                                case 0x3c:
                                    msg = new RequestAllCastleInfo();
                                    break;
                                case 0x3d:
                                    msg = new RequestAllFortressInfo();
                                    break;
                                case 0x3e:
                                    msg = new RequestAllAgitInfo();
                                    break;
                                case 0x3f:
                                    msg = new RequestFortressSiegeInfo();
                                    break;
                                case 0x40:
                                    msg = new RequestGetBossRecord();
                                    break;
                                case 0x41:
                                    msg = new RequestRefine();
                                    break;
                                case 0x42:
                                    msg = new RequestConfirmCancelItem();
                                    break;
                                case 0x43:
                                    msg = new RequestRefineCancel();
                                    break;
                                case 0x44:
                                    msg = new RequestExMagicSkillUseGround();
                                    break;
                                case 0x45:
                                    msg = new RequestDuelSurrender();
                                    break;
                                case 0x46:
                                    msg = new RequestExEnchantSkillInfoDetail();
                                    break;
                                /*case 0x47: ?*/
                                case 0x48:
                                    msg = new RequestFortressMapInfo();
                                    break;
                                case 0x49:
                                    msg = new RequestPVPMatchRecord();
                                    break;
                                case 0x4a:
                                    msg = new SetPrivateStoreWholeMsg();
                                    break;
                                case 0x4b:
                                    msg = new RequestDispel();
                                    break;
                                case 0x4c:
                                    msg = new RequestExTryToPutEnchantTargetItem();
                                    break;
                                case 0x4d:
                                    msg = new RequestExTryToPutEnchantSupportItem();
                                    break;
                                case 0x4e:
                                    msg = new RequestExCancelEnchantItem();
                                    break;
                                case 0x4f:
                                    msg = new RequestChangeNicknameColor();
                                    break;
                                case 0x50:
                                    msg = new RequestResetNickname();
                                    break;
                                case 0x51:
                                    int id4 = buf.getInt();
                                    switch (id4) {
                                        case 0x00:
                                            msg = new RequestBookMarkSlotInfo();
                                            break;
                                        case 0x01:
                                            msg = new RequestSaveBookMarkSlot();
                                            break;
                                        case 0x02:
                                            msg = new RequestModifyBookMarkSlot();
                                            break;
                                        case 0x03:
                                            msg = new RequestDeleteBookMarkSlot();
                                            break;
                                        case 0x04:
                                            msg = new RequestTeleportBookMark();
                                            break;
                                        case 0x05:
                                            break;
                                        default:
                                            client.onUnknownPacket();
                                            break;
                                    }
                                    break;
                                case 0x52:
                                    msg = new RequestWithDrawPremiumItem();
                                    break;
                                case 0x53:
                                    msg = new RequestExJump();
                                    break;
                                case 0x54:
                                    msg = new RequestExStartShowCrataeCubeRank();
                                    break;
                                case 0x55:
                                    msg = new RequestExStopShowCrataeCubeRank();
                                    break;
                                case 0x56:
                                    msg = new NotifyStartMiniGame();
                                    break;
                                case 0x57:
                                    msg = new RequestExJoinDominionWar();
                                    break;
                                case 0x58:
                                    msg = new RequestExDominionInfo();
                                    break;
                                case 0x59:
                                    msg = new RequestExCleftEnter();
                                    break;
                                case 0x5A:
                                    msg = new RequestExCubeGameChangeTeam();
                                    break;
                                case 0x5B:
                                    msg = new RequestExEndScenePlayer();
                                    break;
                                case 0x5C:
                                    msg = new RequestExCubeGameReadyAnswer();
                                    break;
                                case 0x5D:
                                    msg = new RequestExListMpccWaiting();
                                    break;
                                case 0x5E:
                                    msg = new RequestExManageMpccRoom();
                                    break;
                                case 0x5F:
                                    msg = new RequestExJoinMpccRoom();
                                    break;
                                case 0x60:
                                    msg = new RequestExOustFromMpccRoom();
                                    break;
                                case 0x61:
                                    msg = new RequestExDismissMpccRoom();
                                    break;
                                case 0x62:
                                    msg = new RequestExWithdrawMpccRoom();
                                    break;
                                case 0x63:
                                    msg = new RequestExSeedPhase();
                                    break;
                                case 0x64:
                                    msg = new RequestExMpccPartymasterList();
                                    break;
                                case 0x65:
                                    msg = new RequestExPostItemList();
                                    break;
                                case 0x66:
                                    msg = new RequestExSendPost();
                                    break;
                                case 0x67:
                                    msg = new RequestExRequestReceivedPostList();
                                    break;
                                case 0x68:
                                    msg = new RequestExDeleteReceivedPost();
                                    break;
                                case 0x69:
                                    msg = new RequestExRequestReceivedPost();
                                    break;
                                case 0x6A:
                                    msg = new RequestExReceivePost();
                                    break;
                                case 0x6B:
                                    msg = new RequestExRejectPost();
                                    break;
                                case 0x6C:
                                    msg = new RequestExRequestSentPostList();
                                    break;
                                case 0x6D:
                                    msg = new RequestExDeleteSentPost();
                                    break;
                                case 0x6E:
                                    msg = new RequestExRequestSentPost();
                                    break;
                                case 0x6F:
                                    msg = new RequestExCancelSentPost();
                                    break;
                                case 0x70:
                                    msg = new RequestExShowNewUserPetition();
                                    break;
                                case 0x71:
                                    msg = new RequestExShowStepTwo();
                                    break;
                                case 0x72:
                                    msg = new RequestExShowStepThree();
                                    break;
                                case 0x73:
                                    //msg = new ExRaidReserveResult();
                                    break;
                                case 0x75:
                                    msg = new RequestExRefundItem();
                                    break;
                                case 0x76:
                                    msg = new RequestExBuySellUIClose();
                                    break;
                                case 0x77:
                                    msg = new RequestExEventMatchObserverEnd();
                                    break;
                                case 0x78:
                                    msg = new RequestPartyLootModification();
                                    break;
                                case 0x79:
                                    msg = new AnswerPartyLootModification();
                                    break;
                                case 0x7A:
                                    msg = new AnswerCoupleAction();
                                    break;
                                case 0x7B:
                                    msg = new RequestExBR_EventRankerList();
                                    break;
                                case 0x7C:
                                    //msg = new RequestAskMemberShip();
                                    break;
                                case 0x7D:
                                    msg = new RequestAddExpandQuestAlarm();
                                    break;
                                case 0x7E:
                                    msg = new RequestVoteNew();
                                    break;
                                case 0x7F:
                                    _log.info("D0:7F");
                                    break;
                                case 0x80:
                                    _log.info("D0:80");
                                    break;
                                case 0x81:
                                    _log.info("D0:81");
                                    break;
                                case 0x82:
                                    _log.info("D0:82");
                                    break;
                                case 0x83:
                                    int id5 = buf.getInt();
                                    switch (id5) {
                                        /* TODO:
                                         case 0x01:
                                         //msg = new RequestExAgitInitialize chd 0x01
                                         break;
                                         case 0x02:
                                         //msg = new RequestExAgitDetailInfo chdcd 0x02
                                         break;
                                         case 0x03:
                                         //msg = new RequestExMyAgitState chd  0x03
                                         break;
                                         case 0x04:
                                         //msg = new RequestExRegisterAgitForBidStep1 chd 0x04
                                         break;
                                         case 0x05:
                                         //msg = new RequestExRegisterAgitForBidStep2 chddQd 0x05
                                         //msg = new RequestExRegisterAgitForBidStep3 chddQd 0x05 -no error? 0x05
                                         break;
                                         case 0x07:
                                         //msg = new RequestExConfirmCancelRegisteringAgit chd 0x07
                                         break;
                                         case 0x08:
                                         //msg = new RequestExProceedCancelRegisteringAgit chd 0x08
                                         break;
                                         case 0x09:
                                         //msg = new RequestExConfirmCancelAgitBid chdd 0x09
                                         break;
                                         case 0x10:
                                         //msg = new RequestExReBid chdd 0x10
                                         break;
                                         case 0x11:
                                         //msg = new RequestExAgitListForLot chd 0x11
                                         break;
                                         case 0x12:
                                         //msg = new RequestExApplyForAgitLotStep1 chdc 0x12
                                         break;
                                         case 0x13:
                                         //msg = new RequestExApplyForAgitLotStep2 chdc 0x13
                                         break;
                                         case 0x14:
                                         //msg = new RequestExAgitListForBid chdd 0x14
                                         break;
                                         case 0x0D:
                                         //msg = new RequestExApplyForBidStep1 chdd  0x0D
                                         break;
                                         case 0x0E:
                                         //msg = new RequestExApplyForBidStep2 chddQ 0x0E
                                         break;
                                         case 0x0F:
                                         //msg = new RequestExApplyForBidStep3 chddQ 0x0F
                                         break;
                                         case 0x09:
                                         //msg = new RequestExConfirmCancelAgitLot chdc 0x09
                                         break;
                                         case 0x0A:
                                         //msg = new RequestExProceedCancelAgitLot chdc 0x0A
                                         break;
                                         case 0x0A:
                                         //msg = new RequestExProceedCancelAgitBid chdd 0x0A
                                         break;
                                         */
                                    }
                                    break;
                                case 0x84:
                                    msg = new RequestExAddPostFriendForPostBox();
                                    break;
                                case 0x85:
                                    msg = new RequestExDeletePostFriendForPostBox();
                                    break;
                                case 0x86:
                                    msg = new RequestExShowPostFriendListForPostBox();
                                    break;
                                case 0x87:
                                    msg = new RequestExFriendListForPostBox();
                                    break;
                                case 0x88:
                                    msg = new RequestOlympiadMatchList();
                                    break;
                                case 0x89:
                                    msg = new RequestExBR_GamePoint();
                                    break;
                                case 0x8A:
                                    msg = new RequestExBR_ProductList();
                                    break;
                                case 0x8B:
                                    msg = new RequestExBR_ProductInfo();
                                    break;
                                case 0x8C:
                                    msg = new RequestExBR_BuyProduct();
                                    break;
                                case 0x8D:
                                    msg = new RequestExBR_RecentProductList();
                                    break;
                                case 0x8E:
                                    msg = new RequestBR_MiniGameLoadScores();
                                    break;
                                case 0x8F:
                                    msg = new RequestBR_MiniGameInsertScore();
                                    break;
                                case 0x90:
                                    msg = new RequestExBR_LectureMark();
                                    break;
                                case 0x91:
                                    msg = new RequestGoodsInventoryInfo();
                                    break;
                                case 0x92:
                                    //msg = new RequestUseGoodsInventoryItem();
                                    break;
                                default:
                                    client.onUnknownPacket();
                                    break;
                            }
                            break;

                        default: {
                            client.onUnknownPacket();
                            break;
                        }
                    }
                    break;
            }
        } catch (BufferUnderflowException e) {
            client.onPacketReadFail();
        }
        return msg;
    }

    @Override
    public GameClient create(MMOConnection<GameClient> con) {
        return new GameClient(con);
    }

    @Override
    public void execute(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }
}