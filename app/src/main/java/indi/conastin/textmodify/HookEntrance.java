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

public class HookEntrance implements IXposedHookLoadPackage {

    private XSharedPreferences globalSps;
    private XSharedPreferences packageSps;
    // 存放全部规则
    private ArrayList<RuleInfo> allRules = new ArrayList<>();

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("indi.conastin.textmodify")) {
            // hook checkModelActive
            XposedHelpers.findAndHookMethod("indi.conastin.textmodify.MainActivity", lpparam.classLoader, "checkModelActive", XC_MethodReplacement.returnConstant(true));
        } else {
            // 消除附加进程干扰（可能导致异常）
            if (lpparam.isFirstApplication) {
//                Log.d("TextModify", "【HookEntrance】 | spannableString: " + SpannableString.class + " | spannableStringBuilder: " + SpannableStringBuilder.class);
//                Log.d("TextModify", "【HookEntrance】 | packageName: " + lpparam.packageName + " | try to init file");
                // 初始化配置文件
                globalSps = new XSharedPreferences(lpparam.packageName, "global_TextModify");
//                Log.d("TextModify", "【HookEntrance】 | globalFilePath: " + globalSps.getFile() + " | globalFileValues: " + globalSps.getAll());
                packageSps = new XSharedPreferences(lpparam.packageName, lpparam.packageName + "_TextModify");
//                Log.d("TextModify", "【HookEntrance】 | packageFilePath: " + packageSps.getFile() + " | packageFileValues: " + packageSps.getAll());
                // 配置文件读取到globalRules和packageRules
                allRules.addAll(loadRuleList(globalSps));
//                Log.d("TextModify", "【HookEntrance】 | allRules: " + allRules.toString());
                allRules.addAll(loadRuleList(packageSps));
//                Log.d("TextModify", "【HookEntrance】 | allRules: " + allRules.toString());
                // hook text TextView.class method
                XC_MethodHook textviewHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (param.args[0] != null) {
                            if (param.args[0] instanceof String) {
                                String finalText = param.args[0].toString();
//                                Log.d("TextModify", "【HookEntrance】 | String finalText init: " + finalText);
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
//                                        Log.d("TextModify", "【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = finalText;
//                                Log.d("TextModify", "【HookEntrance】 | 处理String字符串: " + param.args[0] + " | finalText: " + finalText);
                            } else if (param.args[0] instanceof SpannableString) {
                                String finalText = param.args[0].toString();
//                                Log.d("TextModify", "【HookEntrance】 | SpannableString finalText init: " + finalText.toString());
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
//                                        Log.d("TextModify", "【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = new SpannableString(finalText);
//                                Log.d("TextModify", "【HookEntrance】 | 处理SpannableString字符串:  " + param.args[0] + " | finalText: " + "cnm");
                            } else if (param.args[0] instanceof SpannableStringBuilder) {
                                SpannableStringBuilder finalText = (SpannableStringBuilder) param.args[0];
//                                Log.d("TextModify", "【HookEntrance】 | SpannableStringBuilder finalText init: " + finalText.toString());
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        // 获取起始位置
                                        int start = param.args[0].toString().indexOf(allRules.get(i).getOriginText());
//                                        Log.d("TextModify", "【HookEntrance】 | start: " + start + " | originText: " + allRules.get(i).getOriginText());
//                                        Log.d("TextModify", "【HookEntrance】 | 比较： " + allRules.get(i).getOriginText().length() + " | 1: " + allRules.get(i).getNewText().length());
                                        // 计算替换文字大小
                                        if (allRules.get(i).getOriginText().length() == allRules.get(i).getNewText().length()) {
                                            // 相等
                                            finalText.replace(start, allRules.get(i).getOriginText().length(), allRules.get(i).getNewText());
                                        } else if (allRules.get(i).getOriginText().length() > allRules.get(i).getNewText().length()) {
                                            // 删减了
                                            finalText.replace(start, allRules.get(i).getNewText().length() + 1, allRules.get(i).getNewText());
//                                            Log.d("TextModify", "【HookEntrance】 | 删减了的finalText: " + finalText + " | method: " + (start + allRules.get(i).getNewText().length()) + ", " + (start + allRules.get(i).getOriginText().length() - allRules.get(i).getOriginText().length()));
                                            finalText.delete(start + allRules.get(i).getNewText().length(), start + allRules.get(i).getOriginText().length() - allRules.get(i).getNewText().length() + 1);
                                        } else {
                                            // 增加了
                                            finalText.replace(start, allRules.get(i).getOriginText().length() + 1, allRules.get(i).getNewText().subSequence(0, allRules.get(i).getOriginText().length()));
//                                            Log.d("TextModify", "【HookEntrance】 | 增加了：" + finalText + " | method: " + allRules.get(i).getNewText().subSequence(0, allRules.get(i).getOriginText().length()));
                                            finalText.insert(start + allRules.get(i).getOriginText().length(), allRules.get(i).getNewText().subSequence(allRules.get(i).getOriginText().length(), allRules.get(i).getNewText().length()));
                                        }
//                                        Log.d("TextModify", "【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = finalText;
//                                Log.d("TextModify", "【HookEntrance】 | 处理SpannableStringBuilder字符串:  " + param.args[0] + " | finalText: " + finalText);
                            } else if (param.args[0] instanceof StringBuffer) {
                                String finalText = param.args[0].toString();
//                                Log.d("TextModify", "【HookEntrance】 | SpannableString finalText init: " + finalText.toString());
                                for (int i = 0; i < allRules.size(); i++) {
                                    if (param.args[0].toString().contains(allRules.get(i).getOriginText())) {
                                        finalText = finalText.replace(allRules.get(i).getOriginText(), allRules.get(i).getNewText());
//                                        Log.d("TextModify", "【HookEntrance】 | origin: " + allRules.get(i).getOriginText() + " | new: " + allRules.get(i).getNewText() + " | finalText: " + finalText);
                                    }
                                }
                                param.args[0] = new StringBuffer(finalText);
//                                Log.d("TextModify", "【HookEntrance】 | 处理StringBuffer字符串:  " + param.args[0] + " | finalText: " + finalText);
                            } else {
                                String origin = param.args[0].toString();
//                                Log.d("TextModify", "【HookEntrance】 | 未知的字符串类型 | 文字: " + origin + " | 类: " + param.args[0].getClass());
                            }
                        }
                    }
                };
                XC_MethodHook spannableStringBuilderHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
//                        Log.d("TextModify", "【HookEntrance】 | params0: " + param.args[0] + " | params1: " + param.args[1] + " | params2: " + param.args[2] + " | params3: " + param.args[3]);
                    }
                };
                XC_MethodHook spannableFactoryHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
//                        Log.d("TextModify", "【HookEntrance】 | params0: " + param.args[0]);
                    }
                };
                XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, textviewHook);
//                XposedHelpers.findAndHookMethod(SpannableS  StringBuilder.class, "setSpan", Object.class, int.class, int.class, int.class, spannableStringBuilderHook);
//                XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, spannableFactoryHook);
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
//                Log.d("TextModify", "【HookEntrance】 | key: " + key + " | key.startsWith('o'): " + key.startsWith("o"));
                if (key.startsWith("o")) {
                    // o
//                    Log.d("TextModify", "【HookEntrance】 | key.substring(1): " + key.substring(1));
//                    Log.d("TextModify", "【HookEntrance】 | map.get(key): " + map.get(key).toString());
//                    Log.d("TextModify", "【HookEntrance】 | temp.get(key.substring(1)): " + temp.get(key.substring(1)));
                    if (temp.get(key.substring(1)) == null) {
                        temp.put(key.substring(1), new RuleInfo(map.get(key).toString(), ""));
                    } else {
                        temp.put(key.substring(1), new RuleInfo(map.get(key).toString(), temp.get(key.substring(1)).getNewText()));
                    }
//                    Log.d("TextModify", "【HookEntrance】 | temp: " + temp.toString());
                } else {
//                    Log.d("TextModify", "【HookEntrance】 | key.substring(1): " + key.substring(1));
//                    Log.d("TextModify", "【HookEntrance】 | map.get(key): " + map.get(key).toString());
//                    Log.d("TextModify", "【HookEntrance】 | temp.get(key.substring(1)): " + temp.get(key.substring(1)));
                    // n
                    if (temp.get(key.substring(1)) == null) {
                        temp.put(key.substring(1), new RuleInfo("", map.get(key).toString()));
                    } else {
                        temp.put(key.substring(1), new RuleInfo(temp.get(key.substring(1)).getOriginText(), map.get(key).toString()));
                    }
//                    Log.d("TextModify", "【HookEntrance】 | temp: " + temp.toString());
                }
            }
        }
//        Log.d("TextModify", "【HookEntrance】 | temp: " + temp.toString());
        // temp键值对转List至ruleList
        for (String key : temp.keySet()) {
            ruleList.add(temp.get(key));
        }
//        Log.d("TextModify", "【HookEntrance】 | ruleList: " + ruleList.toString());
        return ruleList;
    }
}
