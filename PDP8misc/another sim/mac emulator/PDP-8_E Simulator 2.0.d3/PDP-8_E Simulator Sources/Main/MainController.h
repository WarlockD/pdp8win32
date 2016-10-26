/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MainController.h - Main Application Controller Class
 *
 *	This file is part of PDP-8/E Simulator.
 *
 *	PDP-8/E Simulator is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


#import <Cocoa/Cocoa.h>


@class PDP8, BreakpointArray, PluginManager, KeepInMenuWindow;


@interface MainController : NSObject
{
@private
	IBOutlet KeepInMenuWindow	*cpuWindow;
	IBOutlet NSPanel		*preferencesPanel;
	IBOutlet NSPanel		*breakpointPanel;
	IBOutlet NSPanel		*bootstrapPanel;
	IBOutlet NSDrawer		*memoryInspectorDrawer;
	IBOutlet NSView			*loadPaperTapeFieldView;
	IBOutlet NSStepper		*loadPaperTapeFieldStepper;
	IBOutlet PDP8			*pdp8;
	IBOutlet BreakpointArray	*breakpoints;
	IBOutlet BreakpointArray	*breakopcodes;
	IBOutlet PluginManager		*pluginManager;
	BOOL				memoryInspectorVisibleBeforeGo;
	BOOL				breakpointPanelVisibleBeforeGo;
	BOOL				bootstrapPanelVisibleBeforeGo;
}

- (IBAction) reset:(id)sender;
- (IBAction) step:(id)sender;
- (IBAction) trace:(id)sender;
- (IBAction) go:(id)sender;
- (IBAction) stop:(id)sender;
- (IBAction) showPreferencesPanel:(id)sender;
- (IBAction) showHideBreakpointPanel:(id)sender;
- (IBAction) showHideBootstrapPanel:(id)sender;
- (IBAction) toggleMemoryInspectorDrawer:(id)sender;
- (IBAction) clearAllFlags:(id)sender;
- (IBAction) loadExtendedAddress:(id)sender;
- (IBAction) loadPaperTape:(id)sender;
- (IBAction) performZoomAll:(id)sender;

@end
