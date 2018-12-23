package me.limeglass.champions.abilities;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import me.limeglass.champions.abstracts.Ability;
import me.limeglass.champions.objects.ChampionsPlayer;

public class TestAbility extends Ability {

	static {
		//Register the Ability to the Kitpvp plugin.
		//The event is what allows us to check when to execute the ability in the #check(Event event) method below.
		registerAbility(new TestAbility(), PlayerInteractEvent.class);
	}
	
	//Register the name of the ability and the cooldown of the ability.
	//The name is what will be looked for in the kit.yml of the plugin when configuring.
	public TestAbility() {
		super("test", 20);
	}
	
	//Called when the cooldown is finished. Might come in handy so it's here. It's a void so it can be empty.
	@Override
	protected void onCooldownEnd(ChampionsPlayer player) {
		
	}

	//The event that I explained above. This is what triggers the ability. This will be called when the user is involved in the defined event defined when registering.
	//This must return the player so that Champions knows who the ability is for. Return null if the event hasn't matched.
	@Override
	public Player check(Event event) {
		if (((PlayerInteractEvent)event).hasItem()) {
			return ((PlayerInteractEvent)event).getPlayer();
		}
		return null;
	}

	//What to do when the ability is actually executed. This is the main beef of the Ability. The player is whom the ability is for.
	@Override
	public void onAbilityUse(ChampionsPlayer player) {
		Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		fireworkMeta.addEffect(FireworkEffect.builder()
			.with(Type.BALL_LARGE)
			.trail(true)
			.flicker(false)
			.withFade(new Color[]{Color.WHITE, Color.ORANGE})
			.withColor(Color.LIME)
			.build()
		);
		fireworkMeta.setPower(1);
		firework.setFireworkMeta(fireworkMeta);
	}

}
