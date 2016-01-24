QCView-G4
===================================

Quick Circle for AOSP based Roms by BigBoot modified to work on the G4 correctly.

I am not a developer, BigBoot made this possible and has even created an update that allows the y-axis to be offset specifically so we can use this on the G4 - all I've done is push a change with the correct y-axis offset for a G4 build.

Current known issues (CM13, other ROM's untested) - 

Locking/Unlocking sound when closing the cover - this is a conflict to do with CM13's close cover lock action and QCviews own lock action which is necessary for QCview to run. Until an option is added to disable the locking on CM13's end, this will remain an issue. Disabling locking/unlocking sounds means you won't hear the mess of noise and it functions nearly perfectly with this exception. Whether this will have a negative impact on battery life is yet to be seen, but if it does it's going to be pretty minimal.

No compatible call view, so there is no QCview for incoming calls. This is probably out of the scope of what I can achieve to be honest.
