//----------------------------------------------------------------------------
//
//  $Workfile: ShooterSettings.java$
//
//  $Revision: X$
//
//  Project:    FTC 7759 2021
//
//                            Copyright (c) 2021
//                 Cedarcrest High School Team 7759 Auto Prime
//                            All Rights Reserved
//
//  Modification History:
//  $Log:
//  $
//
//  Note:
//
//----------------------------------------------------------------------------
//----------------------------------------------------------------------------
//  Package
//----------------------------------------------------------------------------
package org.firstinspires.ftc.teamcode;

//----------------------------------------------------------------------------
// Class Declarations
//----------------------------------------------------------------------------
//
// Class Name: ShooterSettings
//
// Purpose:
//   Holder for shooting rings
//
//----------------------------------------------------------------------------
public class ShooterSettings {
    
    // ----------------------------------------------------------------------------
    // Class Constants
    // ----------------------------------------------------------------------------
    private final int VEL_IN_TRY_COUNT = 5;
    
    static public final int STATE_WARMUP = 0;
    static public final int STATE_OUT    = 1;
    static public final int STATE_IN     = 2;
    static public final int STATE_REV_UP = 3;
    static public final int STATE_FINISH = 4;
    
    // ----------------------------------------------------------------------------
    // Class Attributes
    // ----------------------------------------------------------------------------
    public double mVelocityIn = -1500;
    public double mVelocityTol = 100;
    public double mOutTime = 1000;
    public double mInTime = 1000;
    public double mVelocityOut = -1500;
    public double mVelocityWait = 1000;
    public int mVelInTry = 0;
    public int mState = 0;
    
    // ----------------------------------------------------------------------------
    // Purpose:
    //  Constructor
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    public ShooterSettings(
        double velocityIn,
        double velocityTol,
        double outTime,
        double inTime,
        double velocityOut,
        double velocityWait) {
            mVelocityIn = velocityIn;
            mVelocityTol = velocityTol;
            mOutTime = outTime;
            mInTime = inTime;
            mVelocityOut = velocityOut;
            mVelocityWait = velocityWait;
        }
        
    // ----------------------------------------------------------------------------
    // Purpose:
    //  Helper for ensuring the shooter is at speed
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    public boolean isSpeedGood(double speed)
    {
        if((mVelocityIn-mVelocityTol) >= speed) {
            if((mVelocityIn+mVelocityTol) <= speed) {
                return true;
            }
        }
        
        mVelInTry++;
        
        if(mVelInTry>VEL_IN_TRY_COUNT) {
            return true;
        }
        
        return false;
    }
}
