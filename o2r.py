#!/usr/bin/env python

#
import getpass
import time,random
import sys
import argparse

obj_file = sys.argv[1]

def export_obj_to_rib(obj_file):
	Round = 6
	# open the file
	ip = open(obj_file,'r')
	#grab the data as lines
	data = ip.readlines()
	groups = []
	group = 'no_group'
	verts = []
	norm = []
	text = []
	face = []
	name = obj_file.split(".")
	name = name[0]
	# for each line check for one of our tokens
	for line in data:
		# we assume that our Tokens are always the first element of the line (which IIRC the spec specifies)
		# so we split each line and look at the first element
		tokens=line.split()	
		# make sure we have a token to check against
		if(len(tokens) >0 ):
			if(tokens[0] == "g"):
				#print "found group "
				group = tokens[1]
				# then add it to our list
				groups += [group]
			elif(tokens[0] == "v"):
				#print "found vert"
				# create a tuple of the vertex point values
				#*****************************************************************
				# NOTE RENDERMAN is left handed coordinate system, obj is right handed -> the z-axis is inverted
				#
				vert = [round(float(tokens[1]),Round),round(float(tokens[2]),Round),(-1) * round(float(tokens[3]),Round)]
				# then add it to our list
				verts[group] += [vert]
			elif(tokens[0] == "vn"):
				#print "found normal"
				# create a tuple of the normal values
				#*****************************************************************
				# NOTE RENDERMAN is left handed coordinate system, obj is right handed -> the z-axis is inverted
				#
				normal = [round(float(tokens[1]),Round),round(float(tokens[2]),Round),(-1) *round(float(tokens[3]),Round)]
				# then add it to our list
				norm[group] += [normal]
			elif(tokens[0] == "vt"):
				#print "found texture"
				# create a tuple of the texture values
				#*****************************************************************
				# NOTE RENDERMAN Maps Textures in the T from top to bottom so we
				# calculate 1.0 - t here so the image will map properly
				#
				tx = [round(float(tokens[1]),Round),1-round(float(tokens[2]),Round)]
				#
				#*****************************************************************
				# then add it to our list
				text[group] += [tx]
			# now we have a face value
			elif(tokens[0] == "f"):
				# add the face to the list and we will process it later (see below)
				face[group] += [line]
			
	# close the file
	ip.close()

	# now we've grabbed all the data we can process each of the faces and write out the rib
	op = open(name + '.rib', 'w')
	
	op.write('##RenderMan RIB-Structure 1.1 Entity')

	#" Should cover obj. files with no groups. Currently however the LXF (LIF) to OBJ exporter writes groups in any case.
	for group in groups:
		op.write('\nAttributeBegin #begin Brick ' + name + '.' + group)
		op.write('\nAttribute \"identifier\" \"uniform string name\" [\"' + name + '.' + group '\"]')
	
		for f in face[group]:
			# create some empty data structures to be filled as we go
			vertices = []
			normals = []
			points = [] 
			tx = []
			fd = f.split() 
			# the face is in the structure shown below Vertex / Texture / Normal. We are gaurenteed to have a
			# Vertex but the others may not be there.
			# 1/1/1 3/2/2 4/3/3 2/4/4

			for perface in fd[1:]:
				index = perface.split("/")
				# get the point array index
				pind = int(index[0])-1
				points.append(round(float(verts[group][pind][0]),Round))
				points.append(round(float(verts[group][pind][1]),Round))
				points.append(round(float(verts[group][pind][2]),Round))
				op.write('\n\tPolygon')
				points_str = " ".join(map(str, points))
				op.write('\n\t\t\"P\" [' + points_str + ']')
				# check for textures and add if there
				if(index[1] != ""):
					tind=int(index[1])-1
					tx.append(round(float(text[group][tind][0]),Round))
					tx.append(round(float(text[group][tind][1]),Round))
					tx_str = " ".join(map(str, tx))
					op.write('\n\t\t\"facevarying float [2] uv1\" [' + tx_str + ']')
				# check for normals and check they are there
				if(index[2] != ""):
					nind=int(index[2])-1
					normals.append(round(float(norm[group][nind][0]),Round))
					normals.append(round(float(norm[group][nind][1]),Round))
					normals.append(round(float(norm[group][nind][2]),Round))
					normals_str = " ".join(map(str, normals))
					op.write('\n\t\t\"N\" [' + normals_str + ']')
					
		op.write('\nAttributeEnd #end Brick ' + name + '.' + group '\n')
		op.close()
			#"
			# create a dictionary to store the polygon data, we always have a point so we can add
			# this directly
			#PolyData={ri.P:points}
			# now see if we have any texture co-ordinates and add them to the dictionary if we do
			#if index[1] !="" :
			#	PolyData[ri.ST]=tx
			# check for normals and add them to the dictionary as well
			#if index[2] !="" :
			#	PolyData[ri.N]=normals
			# finally we generate the Polygon from the data
			#ri.Polygon(PolyData) #{ri.P:points,ri.N:normals,ri.ST:tx})

export_obj_to_rib(obj_file)
