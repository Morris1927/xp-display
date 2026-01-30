package com.xpdisplay;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.text.DecimalFormat;

@Slf4j
@PluginDescriptor(
	name = "XP Display",
	description = "Changes your total level info on your skill page into total XP instead",
	tags = {"total", "level"}
)
public class XPDisplayPlugin extends Plugin
{
	public static final String TOTAL_XP_PREFIX = "Total XP: ";
	public static final DecimalFormat df = new DecimalFormat("#,###");

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;

	@Subscribe
	public void onPluginChanged(PluginChanged pluginChanged)
	{
		if (pluginChanged.getPlugin() == this)
		{
			clientThread.invoke(this::simulateSkillChange);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(this::simulateSkillChange);
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent e)
	{
		final Object[] objectStack = client.getObjectStack();;
		final int objectStackSize = client.getObjectStackSize();

		if (e.getEventName().equalsIgnoreCase(("skillTabTotalLevel"))) {
			long totalXP = 0;

			for ( int xp : client.getSkillExperiences()) {
				totalXP += xp;
			}
			String totalXPString = df.format(totalXP);

			objectStack[objectStackSize-1] = TOTAL_XP_PREFIX + totalXPString;
		}
	}

	private void simulateSkillChange()
	{
		for (Skill skill : Skill.values())
		{
			client.queueChangedSkill(skill);
		}
	}
}
