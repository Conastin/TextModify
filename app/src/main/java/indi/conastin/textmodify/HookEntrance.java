package indi.conastin.textmodify;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import indi.conastin.textmodify.databse.RuleInfo;
import indi.conastin.textmodify.log.NewLog;

public class HookEntrance implements IXposedHookLoadPackage {

    // 存放全部规则
    private final ArrayList<RuleInfo> allRules = new ArrayList<>();

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("indi.conastin.textmodify")) {
            // hook checkModelActive
            XposedHelpers.findAndHookMethod("indi.conastin.textmodify.MainActivity", lpparam.classLoader, "checkModelActive", XC_MethodReplacement.returnConstant(true));
        } else {
            // 消除附加进程干扰（可能导致异常）
            if (lpparam.isFirstApplication) {
                NewLog log = new NewLog();
                log.xposedLog("【HookEntrance】 | packageName: " + lpparam.packageName + " | try to init file");
                // 初始化配置文件
//                XSharedPreferences globalSps = new XSharedPreferences(lpparam.packageName, "global_TextModify");
                XSharedPreferences globalSps = new XSharedPreferences("indi.conastin.textmodify", "global_TextModify");
                globalSps.makeWorldReadable();
                log.xposedLog("【HookEntrance】 | globalFilePath: " + globalSps.getFile() + " | globalFileValues: " + globalSps.getAll());
                XSharedPreferences packageSps = new XSharedPreferences(lpparam.packageName, lpparam.packageName + "_TextModify");
                log.xposedLog("【HookEntrance】 | packageFilePath: " + packageSps.getFile() + " | packageFileValues: " + packageSps.getAll());
                // 配置文件读取到globalRules和packageRules
                allRules.addAll(loadRuleList(globalSps));
                allRules.addAll(loadRuleList(packageSps));
                log.xposedLog("【HookEntrance】 | allRules: " + allRules.toString());
                // hook text TextView.class method
                XC_MethodHook textviewHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (param.args[0] != null) {
                            if (param.args[0] instanceof String) {
                                String finalText = param.args[0].toString();
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        log.xposedLog("【HookEntrance】 | String finalText init: " + finalText);
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
                                        log.xposedLog("【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = finalText;
                            } else if (param.args[0] instanceof SpannableString) {
                                String finalText = param.args[0].toString();
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        log.xposedLog("【HookEntrance】 | SpannableString finalText init: " + finalText);
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
                                        log.xposedLog("【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = new SpannableString(finalText);
                            } else if (param.args[0] instanceof SpannableStringBuilder) {
                                SpannableStringBuilder finalText = (SpannableStringBuilder) param.args[0];
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        log.xposedLog("【HookEntrance】 | SpannableStringBuilder finalText init: " + finalText.toString());
                                        // 获取起始位置
                                        int start = param.args[0].toString().indexOf(allRules.get(i).getOriginText());
                                        // 计算替换文字大小
                                        if (allRules.get(i).getOriginText().length() == allRules.get(i).getNewText().length()) {
                                            // 相等
                                            finalText.replace(start, allRules.get(i).getOriginText().length(), allRules.get(i).getNewText());
                                        } else if (allRules.get(i).getOriginText().length() > allRules.get(i).getNewText().length()) {
                                            // 删减了
                                            finalText.replace(start, allRules.get(i).getNewText().length() + 1, allRules.get(i).getNewText());
                                            finalText.delete(start + allRules.get(i).getNewText().length(), start + allRules.get(i).getOriginText().length() - allRules.get(i).getNewText().length() + 1);
                                        } else {
                                            // 增加了
                                            finalText.replace(start, allRules.get(i).getOriginText().length() + 1, allRules.get(i).getNewText().subSequence(0, allRules.get(i).getOriginText().length()));
                                            finalText.insert(start + allRules.get(i).getOriginText().length(), allRules.get(i).getNewText().subSequence(allRules.get(i).getOriginText().length(), allRules.get(i).getNewText().length()));
                                        }
                                        log.xposedLog("【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = finalText;
                            } else if (param.args[0] instanceof StringBuffer) {
                                String finalText = param.args[0].toString();
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        log.xposedLog("【HookEntrance】 | SpannableString finalText init: " + finalText);
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
                                        log.xposedLog("【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = new StringBuffer(finalText);
                            } else {
                                String origin = param.args[0].toString();
                                log.xposedLog("【HookEntrance】 | 未知的字符串类型 | 文字: " + origin + " | 类: " + param.args[0].getClass());
                            }
                        }
                    }
                };
                XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, textviewHook);
            }
        }
    }

    private ArrayList<RuleInfo> loadRuleList(XSharedPreferences sps) {
        // temp中介 ruleList存放成对的<o,n>列表
        ArrayList<RuleInfo> ruleList = new ArrayList<>();
        Map<String, RuleInfo> temp = new HashMap<>();
        Map<String, ?> map = sps.getAll();
        // map按<o,n>配对存至temp
        for (String key : map.keySet()) {
            // 排除num的干扰 在HookEntrance里面没什么用
            if (!key.equals("num")) {
//                log.xposedLog("【HookEntrance】 | key: " + key + " | key.startsWith('o'): " + key.startsWith("o"));
                if (key.startsWith("o")) {
                    // o
//                    log.xposedLog("【HookEntrance】 | key.substring(1): " + key.substring(1));
//                    log.xposedLog("【HookEntrance】 | map.get(key): " + map.get(key).toString());
//                    log.xposedLog("【HookEntrance】 | temp.get(key.substring(1)): " + temp.get(key.substring(1)));
                    if (temp.get(key.substring(1)) == null) {
                        temp.put(key.substring(1), new RuleInfo(map.get(key).toString(), ""));
                    } else {
                        temp.put(key.substring(1), new RuleInfo(map.get(key).toString(), temp.get(key.substring(1)).getNewText()));
                    }
//                    log.xposedLog("【HookEntrance】 | temp: " + temp.toString());
                } else {
//                    log.xposedLog("【HookEntrance】 | key.substring(1): " + key.substring(1));
//                    log.xposedLog("【HookEntrance】 | map.get(key): " + map.get(key).toString());
//                    log.xposedLog("【HookEntrance】 | temp.get(key.substring(1)): " + temp.get(key.substring(1)));
                    // n
                    if (temp.get(key.substring(1)) == null) {
                        temp.put(key.substring(1), new RuleInfo("", map.get(key).toString()));
                    } else {
                        temp.put(key.substring(1), new RuleInfo(temp.get(key.substring(1)).getOriginText(), map.get(key).toString()));
                    }
//                    log.xposedLog("【HookEntrance】 | temp: " + temp.toString());
                }
            }
        }
//        log.xposedLog("【HookEntrance】 | temp: " + temp.toString());
        // temp键值对转List至ruleList
        for (String key : temp.keySet()) {
            ruleList.add(temp.get(key));
        }
//        log.xposedLog("【HookEntrance】 | ruleList: " + ruleList.toString());
        return ruleList;
    }
}
