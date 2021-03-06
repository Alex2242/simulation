package enstabretagne.travaux_diriges.TD_corrige.MouvementCollisionAvoidance.SimEntity.MouvementSequenceur;

import enstabretagne.base.logger.Logger;
import enstabretagne.base.time.LogicalDuration;
import enstabretagne.simulation.components.IEntity;
import enstabretagne.simulation.components.data.SimFeatures;
import enstabretagne.simulation.core.implementation.SimEvent;
import enstabretagne.travaux_diriges.TD_corrige.MouvementCollisionAvoidance.SimEntity.Robot.Robot;

public class EntityMouvementSequenceurExemple extends EntityMouvementSequenceur{

	EntityMouvementSequenceurFeature emsf;
	EntityMouvementSequenceurInit emsi;
	
	public EntityMouvementSequenceurExemple(String name, SimFeatures features) {
		super(name, features);
	}
	
	@Override
	protected void AfterActivate(IEntity sender, boolean starting) {
		// TODO Auto-generated method stub
		super.AfterActivate(sender, starting);
		
		Robot r = (Robot) getParent();
		Logger.Information(r, "AfterActivate", "EntityMouvementSequenceurExemple, Robot pos is " + r.getPosition().toString());
		
		emsf = (EntityMouvementSequenceurFeature) getFeatures();
		emsi = (EntityMouvementSequenceurInit) getInitParameters();
	}
	
	public class StartMvt extends SimEvent {
		@Override
		public void Process() {
			selfRotator.init(
				getCurrentLogicalDate(),
				mv.getPosition(getCurrentLogicalDate()),
				mv.getRotationXYZ(getCurrentLogicalDate()),
				getTarget(),
				emsf.getMaxSelfRotationSpeed());
			
			mv = selfRotator;
			
			Post(new SelfRotationFinished(), mv.getDurationToReach());			
		}
	}

	class SelfRotationFinished extends SimEvent {

		@Override
		public void Process() {
			if (emsf.getMaxLinearSpeed() != 0) {
				rectilinearMover.init(getCurrentLogicalDate(), mv.getPosition(getCurrentLogicalDate()),
						getTarget(), emsf.getMaxLinearSpeed());
				mv = rectilinearMover;
				Post(new RectilinearMouvementFinished(), mv.getDurationToReach());
			}
		}
	}

	class RectilinearMouvementFinished extends SimEvent {

		@Override
		public void Process() {	
			Logger.Detail(this, "RectilinearMouvementFinished", "Finished : " + getTarget());
			staticMover.init(mv.getPosition(getCurrentLogicalDate()), mv.getRotationXYZ(getCurrentLogicalDate()));
			mv = staticMover;
			
			EntityMouvementSequenceurExemple e= (EntityMouvementSequenceurExemple) Owner();
			Robot r = (Robot) e.getParent();
			
//			Logger.Information(r, "AfterActivate", "Can see table ? " + r.canSeeTable());
//			Logger.Information(r, "AfterActivate", "Vision ? " + r.AcessibleZone().size());
			
			Post(r.new MouvementFinished(), getCurrentLogicalDate().add(LogicalDuration.ofMillis(1)));
		}
	}
}
