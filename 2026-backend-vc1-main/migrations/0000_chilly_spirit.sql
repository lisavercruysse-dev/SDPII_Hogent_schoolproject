CREATE TABLE `afwezigheden` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`start_date` date NOT NULL,
	`end_date` date NOT NULL,
	`days` int NOT NULL,
	`type` varchar(50) NOT NULL,
	`reason` varchar(500) NOT NULL,
	`status` varchar(50) DEFAULT 'In behandeling',
	`can_cancel` boolean NOT NULL DEFAULT true,
	`werknemer_id` int unsigned NOT NULL,
	CONSTRAINT `afwezigheden_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `machines` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`name` varchar(255) NOT NULL,
	`status` varchar(255) NOT NULL,
	`productieStatus` varchar(255) NOT NULL DEFAULT 'GEZOND',
	`productinfo` varchar(500),
	`upTime` int unsigned NOT NULL,
	`datumLaatsteOnderhoud` date,
	`is_deleted` boolean NOT NULL DEFAULT false,
	CONSTRAINT `machines_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `notificaties` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`title` varchar(255) NOT NULL,
	`description` varchar(500) NOT NULL,
	`type` varchar(50) NOT NULL,
	`time` varchar(255) NOT NULL,
	`is_read` boolean NOT NULL DEFAULT false,
	`werknemer_id` int unsigned NOT NULL,
	CONSTRAINT `notificaties_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `site_machines` (
	`siteId` int unsigned NOT NULL,
	`machineId` int unsigned NOT NULL,
	`location` varchar(255) NOT NULL DEFAULT 'Geen locatie',
	CONSTRAINT `site_machines_machineId_siteId_pk` PRIMARY KEY(`machineId`,`siteId`)
);
--> statement-breakpoint
CREATE TABLE `sites` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`name` varchar(255) NOT NULL,
	`capaciteit` int unsigned,
	`operationeleStatus` varchar(255),
	`siteProductieStatus` varchar(255),
	`locatie` varchar(255),
	`land` varchar(255),
	`breedtegraad` decimal(10,8) NOT NULL,
	`lengtegraad` decimal(11,8) NOT NULL,
	`is_deleted` boolean NOT NULL DEFAULT false,
	CONSTRAINT `sites_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `taakTemplates` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`type` varchar(255) NOT NULL,
	`omschrijving` varchar(255) NOT NULL,
	`duurTijd` int unsigned NOT NULL,
	`status` varchar(255) NOT NULL,
	CONSTRAINT `taakTemplates_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `taken` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`werknemer_id` int unsigned NOT NULL,
	`template_id` int unsigned NOT NULL,
	`machine_id` int unsigned NOT NULL,
	`date` datetime NOT NULL,
	`status` varchar(255) NOT NULL,
	`tijdGespendeerd` int unsigned,
	`specificaties` varchar(500) NOT NULL,
	CONSTRAINT `taken_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `team_werknemers` (
	`teamId` int unsigned NOT NULL,
	`werknemerId` int unsigned NOT NULL,
	CONSTRAINT `team_werknemers_teamId_werknemerId_pk` PRIMARY KEY(`teamId`,`werknemerId`)
);
--> statement-breakpoint
CREATE TABLE `teams` (
	`ID` int unsigned AUTO_INCREMENT NOT NULL,
	`NAAM` varchar(255) NOT NULL,
	`SITE_ID` int unsigned NOT NULL,
	`verantwoordelijkeId` int unsigned NOT NULL,
	`status` varchar(255) NOT NULL,
	CONSTRAINT `teams_ID` PRIMARY KEY(`ID`)
);
--> statement-breakpoint
CREATE TABLE `werknemer_Machines` (
	`werknemerId` int unsigned NOT NULL,
	`machineId` int unsigned NOT NULL,
	CONSTRAINT `werknemer_Machines_werknemerId_machineId_pk` PRIMARY KEY(`werknemerId`,`machineId`)
);
--> statement-breakpoint
CREATE TABLE `werknemers` (
	`id` int unsigned AUTO_INCREMENT NOT NULL,
	`firstName` varchar(255) NOT NULL,
	`lastName` varchar(255) NOT NULL,
	`jobTitel` varchar(255) NOT NULL,
	`password_hash` varchar(255) NOT NULL,
	`email` varchar(255) NOT NULL,
	`telefoon` varchar(255),
	`geboortedatum` date NOT NULL,
	`land` varchar(255) NOT NULL,
	`postcode` varchar(255) NOT NULL,
	`stad` varchar(255) NOT NULL,
	`straat` varchar(255) NOT NULL,
	`huisnummer` int unsigned,
	`bus` int,
	`status` varchar(255) NOT NULL,
	CONSTRAINT `werknemers_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
ALTER TABLE `notificaties` ADD CONSTRAINT `notificaties_werknemer_id_werknemers_id_fk` FOREIGN KEY (`werknemer_id`) REFERENCES `werknemers`(`id`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `site_machines` ADD CONSTRAINT `site_machines_siteId_sites_id_fk` FOREIGN KEY (`siteId`) REFERENCES `sites`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `site_machines` ADD CONSTRAINT `site_machines_machineId_machines_id_fk` FOREIGN KEY (`machineId`) REFERENCES `machines`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `taken` ADD CONSTRAINT `taken_werknemer_id_werknemers_id_fk` FOREIGN KEY (`werknemer_id`) REFERENCES `werknemers`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `taken` ADD CONSTRAINT `taken_template_id_taakTemplates_id_fk` FOREIGN KEY (`template_id`) REFERENCES `taakTemplates`(`id`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `taken` ADD CONSTRAINT `taken_machine_id_machines_id_fk` FOREIGN KEY (`machine_id`) REFERENCES `machines`(`id`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `team_werknemers` ADD CONSTRAINT `team_werknemers_teamId_teams_ID_fk` FOREIGN KEY (`teamId`) REFERENCES `teams`(`ID`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `team_werknemers` ADD CONSTRAINT `team_werknemers_werknemerId_werknemers_id_fk` FOREIGN KEY (`werknemerId`) REFERENCES `werknemers`(`id`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `teams` ADD CONSTRAINT `teams_SITE_ID_sites_id_fk` FOREIGN KEY (`SITE_ID`) REFERENCES `sites`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `teams` ADD CONSTRAINT `teams_verantwoordelijkeId_werknemers_id_fk` FOREIGN KEY (`verantwoordelijkeId`) REFERENCES `werknemers`(`id`) ON DELETE no action ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `werknemer_Machines` ADD CONSTRAINT `werknemer_Machines_werknemerId_werknemers_id_fk` FOREIGN KEY (`werknemerId`) REFERENCES `werknemers`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `werknemer_Machines` ADD CONSTRAINT `werknemer_Machines_machineId_machines_id_fk` FOREIGN KEY (`machineId`) REFERENCES `machines`(`id`) ON DELETE cascade ON UPDATE no action;